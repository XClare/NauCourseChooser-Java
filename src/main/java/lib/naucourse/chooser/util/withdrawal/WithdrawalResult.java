package lib.naucourse.chooser.util.withdrawal;

import lib.naucourse.chooser.net.CourseWithdrawal.WithdrawalError;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.SelectedCourse;

import java.io.Serializable;

public class WithdrawalResult implements Serializable {
    private final boolean isSuccess;
    private final SelectedCourse selectedCourse;
    private final CourseType courseType;
    private final WithdrawalError errorCode;
    private final String resultMsg;

    /**
     * 退选课程请求结果
     *
     * @param courseType     课程类型
     * @param selectedCourse 退选课程
     * @param resultMsg      错误元信息
     * @param errorCode      错误代码
     * @param isSuccess      请求是否成功
     */
    public WithdrawalResult(CourseType courseType, SelectedCourse selectedCourse, String resultMsg, WithdrawalError errorCode, boolean isSuccess) {
        this.selectedCourse = selectedCourse;
        this.courseType = courseType;
        this.resultMsg = resultMsg;
        this.errorCode = errorCode;
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public SelectedCourse getSelectedCourse() {
        return selectedCourse;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public WithdrawalError getErrorCode() {
        return errorCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }
}
