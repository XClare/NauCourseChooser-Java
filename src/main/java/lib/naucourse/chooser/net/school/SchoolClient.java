package lib.naucourse.chooser.net.school;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import static lib.naucourse.chooser.net.school.SchoolClient.ClientError.*;

public class SchoolClient {

    /**
     * 教务系统根域名地址
     */
    public static final String JWC_SERVER_URL = "http://jwc.nau.edu.cn/";
    private static final String JWC_LOGOUT_URL = JWC_SERVER_URL + "LoginOut.aspx";
    private static final String SSO_JWC_LOGIN_URL = "http://sso.nau.edu.cn/sso/login?service=http%3a%2f%2fjwc.nau.edu.cn%2fLogin_Single.aspx";
    private static final String SSO_JWC_LOGOUT_URL = "http://sso.nau.edu.cn/sso/logout";
    private static boolean tryingReLogin = false;
    private final OkHttpClient client;
    private final CookieStore cookieStore;
    private final HeaderBuilder headerBuilder;
    private String mainPageUrl = null;
    private String userId = null;
    private String userPw = null;
    private boolean loginStatus = false;

    /**
     * 教务客户端
     *
     * @param cachePath      缓存所在的目录
     * @param connectTimeOut 连接超时（秒）
     * @param readTimeOut    读取超时（秒）
     * @param writeTimeOut   写入超时（秒）
     */
    public SchoolClient(String cachePath, int connectTimeOut, int readTimeOut, int writeTimeOut) {
        cookieStore = new CookieStore();

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.cookieJar(cookieStore);
        clientBuilder.connectTimeout(connectTimeOut, TimeUnit.SECONDS);
        clientBuilder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(writeTimeOut, TimeUnit.SECONDS);
        clientBuilder.cache(new Cache(new File(cachePath), 1024 * 1024));
        clientBuilder.retryOnConnectionFailure(true);
        client = clientBuilder.build();

        this.headerBuilder = new HeaderBuilder();
    }

    /**
     * 检测用用户是否登陆成功
     *
     * @param data 获取的网络数据
     * @return 是否登陆成功
     */
    private static boolean checkUserLogin(String data) {
        return data != null && !data.contains("系统错误提示页") && !data.contains("当前程序在执行过程中出现了未知异常，请重试") && !data.contains("当前你已经登录") && !data.contains("用户登录_南京审计大学教务管理系统") && !data.contains("南京审计大学统一身份认证登录");
    }

    /**
     * 设置用户名和密码
     *
     * @param userId 用户名
     * @param userPw 密码
     */
    public void setUserInfo(String userId, String userPw) {
        this.userId = userId;
        this.userPw = userPw;
    }

    /**
     * 教务登陆
     * 异步请求
     *
     * @param onNetListener 网络获取监听器
     */
    synchronized public void jwcLogin(final OnNetListener onNetListener) {
        if (this.userId == null || this.userPw == null) {
            onNetListener.onError(EMPTY_USER_ID_OR_PW);
        } else if (loginStatus) {
            onNetListener.onError(ALREADY_LOGIN);
        } else {
            cookieStore.clearCookies();
            //首次请求SSO的页面，获取Cookies与Ticket
            client.newCall(new Request.Builder().url(SSO_JWC_LOGIN_URL).build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //超时错误另外处理
                    if (onNetListener != null) {
                        if (e instanceof SocketTimeoutException) {
                            onNetListener.onError(TIME_OUT);
                        } else {
                            onNetListener.onFailure(e);
                        }
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        FormBody postForm = getSSOPostForm(userId, userPw, responseBody.string());
                        response.close();

                        //进行SSO登陆
                        client.newCall(new Request.Builder().url(SSO_JWC_LOGIN_URL).post(postForm).build()).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                if (onNetListener != null) {
                                    if (e instanceof SocketTimeoutException) {
                                        onNetListener.onError(TIME_OUT);
                                    } else {
                                        onNetListener.onFailure(e);
                                    }
                                }
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                ResponseBody responseBody = response.body();
                                if (responseBody != null) {
                                    String body = responseBody.string();
                                    String url = response.request().url().toString();
                                    String query = response.request().url().query();
                                    response.close();
                                    //登陆成功的判断
                                    if (onNetListener != null && (body.contains("密码错误") || body.contains("南京审计大学统一身份认证登录"))) {
                                        onNetListener.onError(PASSWORD);
                                    } else if (onNetListener != null && body.contains("当前你已经登录")) {
                                        onNetListener.onError(ALREADY_LOGIN);
                                    } else if (onNetListener != null && body.contains("请勿输入非法字符")) {
                                        onNetListener.onError(WRONG_SYMBOL);
                                    } else if (body.contains("南京审计大学教学信息管理系统") && query != null && query.contains("r=") && query.contains("&d=")) {
                                        loginStatus = true;
                                        mainPageUrl = url;
                                        if (onNetListener != null) {
                                            onNetListener.onSuccess(url, body);
                                        }
                                    } else if (onNetListener != null) {
                                        onNetListener.onError(LOGIN);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 获取登陆主页的地址
     *
     * @return 地址
     */
    public String getMainPageUrl() {
        return mainPageUrl;
    }

    /**
     * POST数据到一个网页
     * 同步请求，必须自己处理错误
     *
     * @param url      网址
     * @param formBody POST的数据
     * @return POST后得到的数据
     * @throws IOException IOException
     */
    public String postJwcData(final String url, final FormBody formBody) throws IOException {
        if (loginStatus) {
            Response response = client.newCall(headerBuilder.setPostHeader(new Request.Builder().url(url).post(formBody)).build()).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String result = responseBody.string();
                    response.close();
                    return result;
                }
            }
            response.close();
        }
        return null;
    }

    /**
     * 获取教务网页的数据
     * 异步请求
     *
     * @param url           网址
     * @param tryReLogin    如果没有登陆成功则尝试重新登录
     * @param onNetListener 网络获取监听器
     */
    public void getJwcData(final String url, boolean tryReLogin, final OnNetListener onNetListener) {
        if (loginStatus) {
            client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //超时错误另外处理
                    if (onNetListener != null) {
                        if (e instanceof SocketTimeoutException) {
                            onNetListener.onError(TIME_OUT);
                        } else {
                            onNetListener.onFailure(e);
                        }
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String result = responseBody.string();
                        response.close();
                        //检查是否登陆成功
                        if (checkUserLogin(result)) {
                            if (onNetListener != null) {
                                onNetListener.onSuccess(url, result);
                            }
                        } else {
                            //尝试重新登录
                            if (tryReLogin) {
                                tryReLogin(new OnNetListener() {
                                    @Override
                                    public void onSuccess(String netUrl, String html) {
                                        //递归重新获取数据
                                        getJwcData(url, false, onNetListener);
                                    }

                                    @Override
                                    public void onError(ClientError errorCode) {
                                        if (onNetListener != null) {
                                            onNetListener.onError(errorCode);
                                        }
                                    }

                                    @Override
                                    public void onFailure(IOException e) {
                                        if (onNetListener != null) {
                                            onNetListener.onFailure(e);
                                        }
                                    }
                                });
                            } else {
                                if (onNetListener != null) {
                                    onNetListener.onError(NO_LOGIN);
                                }
                            }
                        }
                    } else if (onNetListener != null) {
                        onNetListener.onError(EMPTY_BODY);
                    }
                }
            });
        } else if (onNetListener != null) {
            onNetListener.onError(NO_LOGIN);
        }
    }

    /**
     * 尝试重新登录
     * 监听器onSuccess返回的数据均为null
     *
     * @param onNetListener 网络获取监听器
     */
    synchronized public void tryReLogin(OnNetListener onNetListener) {
        if (tryingReLogin) {
            while (tryingReLogin) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
            if (onNetListener != null) {
                onNetListener.onSuccess(null, null);
            }
        } else {
            tryingReLogin = true;
            jwcLogout(new OnNetListener() {
                @Override
                public void onSuccess(String url, String html) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    jwcLogin(new OnNetListener() {
                        @Override
                        public void onSuccess(String url, String html) {
                            tryingReLogin = false;
                            if (onNetListener != null) {
                                onNetListener.onSuccess(null, null);
                            }
                        }

                        @Override
                        public void onError(ClientError errorCode) {
                            if (onNetListener != null) {
                                onNetListener.onError(errorCode);
                            }
                        }

                        @Override
                        public void onFailure(IOException e) {
                            if (onNetListener != null) {
                                onNetListener.onFailure(e);
                            }
                        }
                    });
                }

                @Override
                public void onError(ClientError errorCode) {
                    if (onNetListener != null) {
                        onNetListener.onError(errorCode);
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    if (onNetListener != null) {
                        onNetListener.onFailure(e);
                    }
                }
            });
        }
    }

    /**
     * 教务系统登出
     * 监听器onSuccess返回的数据均为null
     *
     * @param onNetListener 网络获取监听器
     */
    public void jwcLogout(final OnNetListener onNetListener) {
        client.newCall(new Request.Builder().url(JWC_LOGOUT_URL).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loginStatus = false;
                mainPageUrl = null;
                cookieStore.clearCookies();
                if (onNetListener != null) {
                    onNetListener.onFailure(e);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                response.close();
                client.newCall(new Request.Builder().url(SSO_JWC_LOGOUT_URL).build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        loginStatus = false;
                        mainPageUrl = null;
                        cookieStore.clearCookies();
                        if (onNetListener != null) {
                            onNetListener.onFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        response.close();
                        loginStatus = false;
                        mainPageUrl = null;
                        cookieStore.clearCookies();
                        if (onNetListener != null) {
                            onNetListener.onSuccess(null, null);
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取登录时POST的表单
     *
     * @param userId  用户名
     * @param userPw  用户密码
     * @param ssoHtml SSO获取到的网页
     * @return 表单
     */
    private FormBody getSSOPostForm(String userId, String userPw, String ssoHtml) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", userId);
        formBuilder.add("password", userPw);

        Document document = Jsoup.parse(ssoHtml);
        Elements nameList = document.select("input[name]");
        for (Element element : nameList) {
            String value = element.attr("value");
            String name = element.attr("name");
            if ("lt".equals(name) || "execution".equals(name) || "_eventId".equals(name) || "useVCode".equals(name) || "isUseVCode".equals(name) || "sessionVcode".equals(name) || "errorCount".equals(name)) {
                formBuilder.add(name, value);
            }
        }

        return formBuilder.build();
    }

    public enum ClientError {
        /**
         * 空回复错误
         */
        EMPTY_BODY,
        /**
         * 用户名或密码错误
         */
        PASSWORD,
        /**
         * 已经登陆错误
         */
        ALREADY_LOGIN,
        /**
         * 符号错误
         */
        WRONG_SYMBOL,
        /**
         * 没有登陆错误
         */
        NO_LOGIN,
        /**
         * 用户名或密码为空错误
         */
        EMPTY_USER_ID_OR_PW,
        /**
         * 登陆失败错误
         */
        LOGIN,
        /**
         * 超时错误
         */
        TIME_OUT
    }

    /**
     * 网络获取监听器
     */
    public interface OnNetListener {
        /**
         * 成功时的回调
         *
         * @param url  请求的网址
         * @param html 请求到的网页内容
         */
        void onSuccess(String url, String html);

        /**
         * 错误时的回调
         *
         * @param errorCode 错误代码
         */
        void onError(ClientError errorCode);

        /**
         * IOException时的回调
         *
         * @param e IOException
         */
        void onFailure(IOException e);
    }
}
