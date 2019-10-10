package lib.naucourse.chooser.net.school;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class CookieStore implements CookieJar {
    private final HashMap<String, List<Cookie>> cookieMap;

    /**
     * Cookies保存
     */
    CookieStore() {
        this.cookieMap = new HashMap<>();
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        cookieMap.put(httpUrl.host(), list);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieMap.get(httpUrl.host());
        return cookies != null ? cookies : new ArrayList<>();
    }

    /**
     * 清空Cookies
     */
    void clearCookies() {
        cookieMap.clear();
    }
}
