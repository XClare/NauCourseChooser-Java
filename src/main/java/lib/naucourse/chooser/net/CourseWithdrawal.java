package lib.naucourse.chooser.net;

import lib.naucourse.chooser.net.callable.WithdrawalUnitCallable;
import lib.naucourse.chooser.net.school.SchoolClient;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.SelectedCourse;
import lib.naucourse.chooser.util.withdrawal.WithdrawalResult;
import lib.naucourse.chooser.util.withdrawal.WithdrawalUnit;
import okhttp3.FormBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CourseWithdrawal {
    /**
     * 未知错误
     */
    public static final int ERROR_UNKNOWN = 0;
    /**
     * 退选课程列表错误
     */
    public static final int ERROR_COURSE_LIST = 1;
    /**
     * 数据请求错误
     */
    public static final int ERROR_DATA_POST = 2;
    /**
     * 超时错误
     */
    public static final int ERROR_TIME_OUT = 3;

    private static final String URL = SchoolClient.JWC_SERVER_URL + "Servlet/DeleteCourseInfo.ashx";
    private final SchoolClient schoolClient;
    private final ExecutorService pool;
    private int submitResultGetCount = 0;

    /**
     * 用于退选课程
     *
     * @param schoolClient 教务系统客户端
     */
    public CourseWithdrawal(SchoolClient schoolClient) {
        this.schoolClient = schoolClient;
        this.pool = Executors.newCachedThreadPool();
    }

    /**
     * 提交退选课程
     *
     * @param selectedCourseMap    退选课程
     * @param onWithdrawalListener 退选课程监听器
     */
    synchronized public void submit(final Map<CourseType, ArrayList<SelectedCourse>> selectedCourseMap, final OnWithdrawalListener onWithdrawalListener) {
        if (selectedCourseMap != null) {
            submitResultGetCount = 0;
            pool.submit(() -> {
                ArrayList<Future<WithdrawalResult>> withdrawalSubmitList = new ArrayList<>();
                for (CourseType courseType : selectedCourseMap.keySet()) {
                    List<SelectedCourse> selectedCourses = selectedCourseMap.get(courseType);
                    for (SelectedCourse selectedCourse : selectedCourses) {
                        WithdrawalUnit withdrawalUnit = new WithdrawalUnit(selectedCourse, courseType, getPostForm(courseType, selectedCourse), URL);
                        withdrawalSubmitList.add(pool.submit(new WithdrawalUnitCallable(schoolClient, withdrawalUnit)));
                    }
                }
                for (Future<WithdrawalResult> future : withdrawalSubmitList) {
                    WithdrawalResult withdrawalResult = null;
                    boolean error = false;
                    try {
                        withdrawalResult = future.get();
                    } catch (InterruptedException ignored) {
                    } catch (ExecutionException e) {
                        future.cancel(true);
                        error = true;
                    }
                    submitResultGetCount++;
                    if (onWithdrawalListener != null) {
                        if (error) {
                            onWithdrawalListener.onFailed(ERROR_DATA_POST);
                        } else {
                            onWithdrawalListener.onSubmitSuccess(withdrawalResult);
                        }
                        if (submitResultGetCount >= withdrawalSubmitList.size()) {
                            System.gc();
                            onWithdrawalListener.onSubmitFinish();
                        }
                    }
                }
            });
        } else if (onWithdrawalListener != null) {
            onWithdrawalListener.onFailed(ERROR_COURSE_LIST);
        }
    }

    /**
     * 停止退选课程
     */
    public void stopSubmit() {
        if (!pool.isShutdown()) {
            pool.shutdownNow();
        }
    }

    /**
     * 获取课程退选的表单
     *
     * @param courseType     课程类别
     * @param selectedCourse 课程
     * @return 表单
     */
    private FormBody getPostForm(CourseType courseType, SelectedCourse selectedCourse) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("id", selectedCourse.getPostId());
        formBuilder.add("startDate", courseType.getStartDate());
        formBuilder.add("endDate", courseType.getEndDate());
        formBuilder.add("tc", selectedCourse.getPostTc());
        formBuilder.add("c", selectedCourse.getPostc());
        formBuilder.add("tm", selectedCourse.getPostTm());
        return formBuilder.build();
    }

    /**
     * 退选课程监听器
     */
    public interface OnWithdrawalListener {
        /**
         * 退选提交成功时的回调
         * 会调用多次
         *
         * @param withdrawalResult 退选结果
         */
        void onSubmitSuccess(WithdrawalResult withdrawalResult);

        /**
         * 退选提交结束时的回调
         * 只会调用一次
         */
        void onSubmitFinish();

        /**
         * 退选发生错误时的回调
         * 会调用多次
         *
         * @param errorCode 错误代码
         */
        void onFailed(int errorCode);
    }
}
