package lib.naucourse.chooser.util;

import java.io.Serializable;

/**
 * 已选课程
 */
public class SelectedCourse implements Serializable {
    private int index;
    private String name;
    private String teacher;
    private String teachingClass;
    private float score;
    private String courseId;
    private String college;
    private String selectTime;
    private String courseType;
    private int postIndex;
    private String postId;
    private String postTc;
    private String postc;
    private String postTm;

    /**
     * 获取课程序号
     *
     * @return 课程序号
     */
    public int getIndex() {
        return index;
    }

    /**
     * 设置课程序号
     *
     * @param index 课程序号
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 获取课程名称
     *
     * @return 课程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置课程名称
     *
     * @param name 课程名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取教学班名称
     *
     * @return 教学班名称
     */
    public String getTeachingClass() {
        return teachingClass;
    }

    /**
     * 设置教学班名称
     *
     * @param teachingClass 教学班名称
     */
    public void setTeachingClass(String teachingClass) {
        this.teachingClass = teachingClass;
    }

    /**
     * 获取学分
     *
     * @return 学分
     */
    public float getScore() {
        return score;
    }

    /**
     * 设置学分
     *
     * @param score 学分
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * 获取开课学院
     *
     * @return 开课学院
     */
    public String getCollege() {
        return college;
    }

    /**
     * 设置开课学院
     *
     * @param college 开课学院
     */
    public void setCollege(String college) {
        this.college = college;
    }

    /**
     * 获取课程ID
     *
     * @return 课程ID
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * 设置课程ID
     *
     * @param courseId 课程ID
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * 获取选课时间
     *
     * @return 选课时间
     */
    public String getSelectTime() {
        return selectTime;
    }

    /**
     * 设置选课时间
     *
     * @param selectTime 选课时间
     */
    public void setSelectTime(String selectTime) {
        this.selectTime = selectTime;
    }

    /**
     * 获取用于POST的课程tc
     * 仅用于POST
     *
     * @return 用于POST的课程tc
     */
    public String getPostTc() {
        return postTc;
    }

    /**
     * 设置用于POST的课程tc
     * 仅用于POST
     *
     * @param postTc 用于POST的课程tc
     */
    public void setPostTc(String postTc) {
        this.postTc = postTc;
    }

    /**
     * 获取用于POST的课程c
     * 仅用于POST
     *
     * @return 用于POST的课程c
     */
    public String getPostc() {
        return postc;
    }

    /**
     * 设置用于POST的课程c
     * 仅用于POST
     *
     * @param postc 用于POST的课程c
     */
    public void setPostc(String postc) {
        this.postc = postc;
    }

    /**
     * 获取用于POST的课程tm
     * 仅用于POST
     *
     * @return 用于POST的课程tm
     */
    public String getPostTm() {
        return postTm;
    }

    /**
     * 设置用于POST的课程tm
     * 仅用于POST
     *
     * @param postTm 用于POST的课程tm
     */
    public void setPostTm(String postTm) {
        this.postTm = postTm;
    }

    /**
     * 获取教师姓名
     *
     * @return 教师姓名
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * 设置教师姓名
     *
     * @param teacher 教师姓名
     */
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    /**
     * 获取课程类别
     *
     * @return 课程类别
     */
    public String getCourseType() {
        return courseType;
    }

    /**
     * 设置课程类别
     *
     * @param courseType 课程类别
     */
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    /**
     * 获取用于POST的课程Index
     * 仅用于POST
     *
     * @return 用于POST的课程Index
     */
    public int getPostIndex() {
        return postIndex;
    }

    /**
     * 设置用于POST的课程Index
     * 仅用于POST
     *
     * @param postIndex 用于POST的课程Index
     */
    public void setPostIndex(int postIndex) {
        this.postIndex = postIndex;
    }

    /**
     * 获取用于POST的课程id
     * 仅用于POST
     *
     * @return 用于POST的课程id
     */
    public String getPostId() {
        return postId;
    }

    /**
     * 设置用于POST的课程id
     * 仅用于POST
     *
     * @param postId 用于POST的课程id
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }
}
