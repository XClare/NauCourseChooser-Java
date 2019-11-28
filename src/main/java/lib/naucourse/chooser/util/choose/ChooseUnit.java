package lib.naucourse.chooser.util.choose;

import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;
import okhttp3.FormBody;

import java.io.Serializable;

public class ChooseUnit implements Serializable {
    private final Course course;
    private final CourseType courseType;
    private final FormBody formBody;
    private final String url;
    private final boolean isSubCourse;

    /**
     * 提交课程的单元
     *
     * @param courseType  课程类别
     * @param course      课程
     * @param formBody    表单
     * @param url         地址
     * @param isSubCourse 是否是备选课程
     */
    public ChooseUnit(CourseType courseType, Course course, FormBody formBody, String url, boolean isSubCourse) {
        this.course = course;
        this.courseType = courseType;
        this.formBody = formBody;
        this.url = url;
        this.isSubCourse = isSubCourse;
    }

    public FormBody getFormBody() {
        return formBody;
    }

    public String getUrl() {
        return url;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isSubCourse() {
        return isSubCourse;
    }
}