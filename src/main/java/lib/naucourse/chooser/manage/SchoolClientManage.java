package lib.naucourse.chooser.manage;

import lib.naucourse.chooser.net.CourseChoose;
import lib.naucourse.chooser.net.CourseList;
import lib.naucourse.chooser.net.CourseWithdrawal;
import lib.naucourse.chooser.net.coursetype.*;
import lib.naucourse.chooser.net.school.SchoolClient;
import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.DataPath;
import lib.naucourse.chooser.util.SelectedCourse;
import lib.naucourse.chooser.util.choose.ChooseInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static lib.naucourse.chooser.net.school.SchoolClient.ClientError.ALREADY_LOGIN;

public class SchoolClientManage {
    private final SchoolClient schoolClient;
    private final CourseList courseList;
    private final CourseChoose courseChoose;
    private final CourseWithdrawal courseWithdrawal;


    /**
     * 用于教务系统管理
     *
     * @param dataPath       数据文件路径
     * @param connectTimeOut 连接超时（秒）
     * @param readTimeOut    读取超时（秒）
     * @param writeTimeOut   写入超时（秒）
     */
    public SchoolClientManage(DataPath dataPath, int connectTimeOut, int readTimeOut, int writeTimeOut) {
        this.schoolClient = new SchoolClient(dataPath.getCachePath(), connectTimeOut, readTimeOut, writeTimeOut);
        this.courseList = new CourseList(schoolClient, new DefaultAnalyseCourseType());
        this.courseList.addNewCourseType(new EnglishFollowAnalyseCourse(),
                new MajorAnalyseCourse(),
                new SchoolRetakeAnalyseCourse());
        this.courseChoose = new CourseChoose(schoolClient);
        this.courseWithdrawal = new CourseWithdrawal(schoolClient);
    }

    /**
     * 增加新的课程类别的分析器
     *
     * @param courseTypes 课程类别分析
     */
    public void addNewAnalyseType(AnalyseCourseType... courseTypes) {
        this.courseList.addNewCourseType(courseTypes);
    }

    /**
     * 登陆教务系统
     *
     * @param userId          用户名
     * @param userPw          用户密码
     * @param tryReLogin      如果已经登陆，是否尝试自动重新登录
     * @param onLoginListener 登陆监听
     */
    synchronized public void login(final String userId, final String userPw, final boolean tryReLogin, final OnLoginListener onLoginListener) {
        this.schoolClient.setUserInfo(userId, userPw);
        this.schoolClient.jwcLogin(new SchoolClient.OnNetListener() {
            @Override
            public void onSuccess(String url, String html) {
                if (onLoginListener != null) {
                    if (onLoginListener.onLogin(url)) {
                        logout(null);
                    }
                }
            }

            @Override
            public void onError(SchoolClient.ClientError errorCode) {
                if (errorCode == ALREADY_LOGIN && tryReLogin) {
                    schoolClient.jwcLogout(null);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    login(userId, userPw, false, onLoginListener);
                } else {
                    if (onLoginListener != null) {
                        onLoginListener.onFailed();
                    }
                }
            }

            @Override
            public void onFailure(IOException e) {
                if (onLoginListener != null) {
                    onLoginListener.onFailed();
                }
            }
        });
    }

    /**
     * 提交选课
     *
     * @param submitInfo       提交课程的信息
     * @param chooseCourseList 选择的课程
     * @param onSubmitListener 提交的监听器
     */
    public void submitChooseCourse(ChooseInfo submitInfo, LinkedHashMap<CourseType, HashMap<String, ArrayList<Course>>> chooseCourseList, CourseChoose.OnSubmitListener onSubmitListener) {
        this.courseChoose.submit(submitInfo, chooseCourseList, onSubmitListener);
    }

    /**
     * 停止选课
     */
    public void stopSubmitChooseCourse() {
        this.courseChoose.stopSubmit();
    }

    /**
     * 提交退选
     *
     * @param selectedCourseMap    退选的课程
     * @param onWithdrawalListener 退选的监听器
     */
    public void submitWithdrawalCourse(LinkedHashMap<CourseType, ArrayList<SelectedCourse>> selectedCourseMap, CourseWithdrawal.OnWithdrawalListener onWithdrawalListener) {
        this.courseWithdrawal.submit(selectedCourseMap, onWithdrawalListener);
    }

    /**
     * 停止退课
     */
    public void stopSubmitWithdrawalCourse() {
        this.courseWithdrawal.stopSubmit();
    }

    /**
     * 获取课程类别列表
     *
     * @param courseTypeListener 获取课程类别列表的监听器
     */
    public void getCourseType(CourseList.OnCourseTypeListener courseTypeListener) {
        this.courseList.getCourseTypeList(courseTypeListener);
    }

    /**
     * 获取课程列表
     *
     * @param courseType         课程类别
     * @param courseListListener 获取课程列表的监听器
     */
    public void getCourseList(CourseType courseType, CourseList.OnCourseListListener courseListListener) {
        this.courseList.getCourseList(courseType, courseListListener);
    }

    /**
     * 登出教务系统
     *
     * @param onLogoutListener 教务系统登出的监听器
     */
    public void logout(OnLogoutListener onLogoutListener) {
        this.schoolClient.jwcLogout(new SchoolClient.OnNetListener() {
            @Override
            public void onSuccess(String url, String html) {
                if (onLogoutListener != null) {
                    onLogoutListener.onLogout();
                }
            }

            @Override
            public void onError(SchoolClient.ClientError errorCode) {
                if (onLogoutListener != null) {
                    onLogoutListener.onFailed();
                }
            }

            @Override
            public void onFailure(IOException e) {
                if (onLogoutListener != null) {
                    onLogoutListener.onFailed();
                }
            }
        });
    }

    /**
     * 教务系统登陆监听器
     */
    public interface OnLoginListener {
        /**
         * 登陆成功的回调
         *
         * @param userUrl 教务系统首页地址
         * @return 是否直接登出
         */
        boolean onLogin(String userUrl);

        /**
         * 登陆错误的回调
         */
        void onFailed();
    }

    /**
     * 教务系统登出监听器
     */
    public interface OnLogoutListener {
        /**
         * 登出成功的回调
         */
        void onLogout();

        /**
         * 登出失败的回调
         */
        void onFailed();
    }
}
