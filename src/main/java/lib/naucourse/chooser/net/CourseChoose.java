package lib.naucourse.chooser.net;

import lib.naucourse.chooser.net.callable.ChooseTypeCallable;
import lib.naucourse.chooser.net.school.SchoolClient;
import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.choose.ChooseInfo;
import lib.naucourse.chooser.util.choose.ChooseResult;
import lib.naucourse.chooser.util.choose.ChooseUnit;
import okhttp3.FormBody;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CourseChoose extends CourseSubmit {
    /**
     * 主要选课课程类型
     */
    public static final String CHOOSE_SUBMIT_TYPE_MAIN = "MainCourse";
    /**
     * 备选选课课程类型
     */
    public static final String CHOOSE_SUBMIT_TYPE_SUB = "SubCourse";

    /**
     * 用于提交选课
     *
     * @param schoolClient 教务系统客户端
     */
    public CourseChoose(SchoolClient schoolClient) {
        super(schoolClient);
    }

    private static Hashtable<CourseType, Hashtable<String, ArrayList<ChooseUnit>>> prepareSubmitMap(final LinkedHashMap<CourseType, HashMap<String, ArrayList<Course>>> chooseCourseMap) {
        Hashtable<CourseType, Hashtable<String, ArrayList<ChooseUnit>>> submitMap = new Hashtable<>();

        for (CourseType courseType : chooseCourseMap.keySet()) {
            Map<String, ArrayList<Course>> courseHashMap = chooseCourseMap.get(courseType);

            Hashtable<String, ArrayList<ChooseUnit>> submitTable = new Hashtable<>();
            ArrayList<ChooseUnit> mainSubmitList = new ArrayList<>();
            ArrayList<ChooseUnit> subSubmitList = new ArrayList<>();

            if (courseHashMap.containsKey(CHOOSE_SUBMIT_TYPE_MAIN)) {
                addToSubmitList(mainSubmitList, courseHashMap.get(CHOOSE_SUBMIT_TYPE_MAIN), courseType, false);
            }
            if (courseHashMap.containsKey(CHOOSE_SUBMIT_TYPE_SUB)) {
                addToSubmitList(subSubmitList, courseHashMap.get(CHOOSE_SUBMIT_TYPE_SUB), courseType, true);
            }

            submitTable.put(CHOOSE_SUBMIT_TYPE_MAIN, mainSubmitList);
            submitTable.put(CHOOSE_SUBMIT_TYPE_SUB, subSubmitList);
            submitMap.put(courseType, submitTable);
        }

        return submitMap;
    }

    /**
     * 将需要提交的课程转为提交的格式加入提交列表
     *
     * @param submitList 提交列表
     * @param courses    课程
     * @param courseType 课程类别
     */
    private static void addToSubmitList(List<ChooseUnit> submitList, List<Course> courses, CourseType
            courseType, boolean isSubCourse) {
        for (Course course : courses) {
            submitList.add(new ChooseUnit(courseType, course, getPostForm(courseType, course), getPostUrl(courseType), isSubCourse));
        }
    }

    /**
     * 获取课程提交的表单
     *
     * @param courseType 课程类别
     * @param course     课程
     * @return 表单
     */
    private static FormBody getPostForm(CourseType courseType, Course course) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("courseID", course.getCourseId());
        formBuilder.add("teachingClass", course.getTeachingClass());
        formBuilder.add("term", courseType.getTerm());
        formBuilder.add("startDate", courseType.getStartDateCode());
        formBuilder.add("endDate", courseType.getEndDateCode());
        formBuilder.add("limitNum", String.valueOf(courseType.getBatchAvailableNum()));
        formBuilder.add("CourseSelectStyle", courseType.getCourseSelectStyle());
        formBuilder.add("banRule", courseType.getBanRule());
        return formBuilder.build();
    }

    /**
     * 获取提交的网址
     *
     * @param courseType 课程类别
     * @return 网址
     */
    private static String getPostUrl(CourseType courseType) {
        switch (courseType.getSubmitType()) {
            case CourseList.SUBMIT_TYPE_ENG_EXPAND:
                return SchoolClient.JWC_SERVER_URL + "Servlet/AddEngCourseSelectModel.ashx";
            case CourseList.SUBMIT_TYPE_REPAIR_GROUP:
                return SchoolClient.JWC_SERVER_URL + "Servlet/AddRepairGroupCourseSelectModel.ashx";
            case CourseList.SUBMIT_TYPE_ZX:
            case CourseList.SUBMIT_TYPE_NORMAL:
            default:
                return SchoolClient.JWC_SERVER_URL + "Servlet/AddCourseSelectModel.ashx";
        }
    }

    /**
     * 提交选课
     * 提交选课时不会检验是否已经登出，所以不能够断网换网
     *
     * @param submitInfo       提交的信息配置
     * @param chooseCourseMap  选择的课程
     * @param onSubmitListener 提交监听器
     * @return 是否提交成功（同一时刻只能提交一次）
     */
    synchronized public boolean submit(final ChooseInfo submitInfo, final LinkedHashMap<CourseType, HashMap<String, ArrayList<Course>>> chooseCourseMap, final OnSubmitListener onSubmitListener) {
        if (submitLock.tryLock()) {
            try {
                stopSubmit = false;
                if (chooseCourseMap != null) {
                    submitResultGetCount = 0;
                    pool.submit(() -> {
                        try {
                            //获取提交列表
                            Hashtable<CourseType, Hashtable<String, ArrayList<ChooseUnit>>> submitMap = prepareSubmitMap(chooseCourseMap);

                            //按类别整理为提交的类型提交课程
                            for (int turn = 0; turn < submitInfo.getTotalSubmitTurn(); turn++) {
                                if (stopSubmit) {
                                    stopSubmit = false;
                                    break;
                                }
                                ArrayList<Future<Void>> submitResultFutureArrayList = new ArrayList<>();

                                for (Hashtable<String, ArrayList<ChooseUnit>> submitTable : submitMap.values()) {
                                    if (stopSubmit) {
                                        stopSubmit = false;
                                        break;
                                    }
                                    //将提交的结果归总
                                    ChooseTypeCallable typeCallable = new ChooseTypeCallable(pool, schoolClient, submitInfo, submitTable.get(CHOOSE_SUBMIT_TYPE_MAIN), submitTable.get(CHOOSE_SUBMIT_TYPE_SUB));
                                    if (onSubmitListener != null) {
                                        typeCallable.setCallBack(turn + 1, onSubmitListener);
                                    }
                                    Future<Void> listFuture = pool.submit(typeCallable);
                                    submitResultFutureArrayList.add(listFuture);
                                }

                                if (onSubmitListener != null) {
                                    if (submitInfo.isAutoRemoveMode() || submitInfo.isScanCourseMode()) {
                                        onSubmitListener.onSubmitProcess(1, 1);
                                    } else {
                                        onSubmitListener.onSubmitProcess(turn + 1, submitInfo.getTotalSubmitTurn());
                                    }
                                }

                                //等待一轮的提交的结果
                                boolean error = false;
                                for (Future<Void> listFuture : submitResultFutureArrayList) {
                                    if (error) {
                                        listFuture.cancel(true);
                                    } else {
                                        try {
                                            listFuture.get();
                                        } catch (InterruptedException ignored) {
                                        } catch (ExecutionException e) {
                                            listFuture.cancel(true);
                                            error = true;
                                        }
                                    }
                                    if (stopSubmit) {
                                        stopSubmit = false;
                                        break;
                                    }
                                }
                                submitResultGetCount++;
                                if (onSubmitListener != null) {
                                    if (error) {
                                        onSubmitListener.onFailed(turn + 1, CourseError.DATA_POST);
                                    }
                                    if (submitResultGetCount >= submitInfo.getTotalSubmitTurn()) {
                                        System.gc();
                                        onSubmitListener.onSubmitFinish();
                                    }
                                }

                                //自动移除模式与扫课模式下只进行一轮
                                if (submitInfo.isAutoRemoveMode() || submitInfo.isScanCourseMode()) {
                                    System.gc();
                                    if (onSubmitListener != null) {
                                        onSubmitListener.onSubmitFinish();
                                    }
                                    break;
                                }
                            }
                        } finally {
                            submitLock.unlock();
                        }
                    });
                } else if (onSubmitListener != null) {
                    onSubmitListener.onFailed(0, CourseError.COURSE_LIST);
                }
            } catch (Exception pass) {
                submitLock.unlock();
            }
            return true;
        }
        return false;
    }

    public enum CourseError {
        /**
         * 系统请求错误
         */
        SYSTEM_REQUEST,
        /**
         * 未知错误
         */
        UNKNOWN,
        /**
         * 不再指定选课时间内错误
         */
        NOT_IN_TIME,
        /**
         * 已经选了此类课程错误
         */
        HAS_SELECTED,
        /**
         * 已经学过类似课程错误
         */
        HAS_STUDIED_SAME_COURSE,
        /**
         * 选课超出限制错误
         */
        SELECT_OUT_OF_LIMIT,
        /**
         * 该类型课程已经选满错误
         */
        ENOUGH_SELECTED,
        /**
         * 选课课程已经选满错误
         */
        FULL_SELECTED,
        /**
         * 超出学分限制错误
         */
        MAX_SCORE,
        /**
         * 与课程表课程冲突错误
         */
        CONFLICT_WITH_TABLE_COURSE,
        /**
         * 与选的其他课程冲突错误
         */
        CONFLICT_WITH_SELECTED_COURSE,
        /**
         * 不能够选课的错误
         */
        NOT_ABLE_TO_CHOOSE,

        /**
         * 选课课程列表错误
         */
        COURSE_LIST,
        /**
         * 数据请求错误
         */
        DATA_POST,
        /**
         * 超时错误
         */
        TIME_OUT
    }

    /**
     * 提交课程监听器
     */
    public interface OnSubmitListener {
        /**
         * 提交课程进度
         * 会调用多次
         *
         * @param nowTurn   当前第几轮
         * @param totalTurn 总共几轮
         */
        void onSubmitProcess(int nowTurn, int totalTurn);

        /**
         * 一次成功提交时的回调
         * 会调用多次
         *
         * @param turn         第几轮
         * @param chooseResult 提交结果列表
         */
        void onSubmitResult(int turn, ChooseResult chooseResult);

        /**
         * 所有提交都成功时的回调
         * 只会调用一次
         */
        void onSubmitFinish();

        /**
         * 一轮发生错误时的回调
         * 会调用多次
         *
         * @param nowTurn   第几轮
         * @param errorCode 错误代码
         */
        void onFailed(int nowTurn, CourseError errorCode);
    }
}
