package lib.naucourse.chooser.util.choose;

import lib.naucourse.chooser.net.CourseChoose;
import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;

import java.io.Serializable;

public class ChooseResult implements Serializable {
    private final Course course;
    private final CourseType courseType;
    private final CourseChoose.CourseError errorCode;
    private final boolean isSuccess;
    private final boolean isSubCourse;
    private final String resultMsg;
    private boolean willAutoRemove;

    /**
     * 课程提交请求结果
     *
     * @param courseType  课程类型
     * @param course      课程
     * @param resultMsg   返回元内容（可能为空）
     * @param errorCode   错误代码
     * @param isSuccess   是否是请求成功
     * @param isSubCourse 是否是备选课程
     */
    public ChooseResult(CourseType courseType, Course course, String resultMsg, CourseChoose.CourseError errorCode, boolean isSuccess, boolean isSubCourse) {
        this.errorCode = errorCode;
        this.courseType = courseType;
        this.course = course;
        this.isSuccess = isSuccess;
        this.isSubCourse = isSubCourse;
        this.willAutoRemove = false;
        this.resultMsg = resultMsg;
    }

    public Course getCourse() {
        return course;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public CourseChoose.CourseError getErrorCode() {
        return errorCode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isSubCourse() {
        return isSubCourse;
    }

    public boolean isWillAutoRemove() {
        return willAutoRemove;
    }

    /**
     * 本次提交是否在同轮下一次提交时被自动移除
     *
     * @param willAutoRemove 是否被自动移除
     */
    public void setWillAutoRemove(boolean willAutoRemove) {
        this.willAutoRemove = willAutoRemove;
    }

    public String getResultMsg() {
        return resultMsg;
    }
}