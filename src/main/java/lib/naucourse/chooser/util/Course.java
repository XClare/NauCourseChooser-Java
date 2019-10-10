package lib.naucourse.chooser.util;

import java.io.Serializable;

public class Course implements Serializable {
    private int index;
    private String name;
    private float score;
    private String college;
    private String teacher;
    private String time;
    private String description;
    private int batch;
    private boolean isInfinite;
    private int availableNum;
    private int selectedNum;
    private boolean available;
    private String courseProperty;
    // 必须填写
    private String courseId;
    private String teachingClass;

    /**
     * 课程
     */
    public Course() {
        this.available = true;
    }

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
     * 获取上课时间
     *
     * @return 上课时间
     */
    public String getTime() {
        return time;
    }

    /**
     * 设置上课时间
     *
     * @param time 上课时间
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 获取课程介绍
     *
     * @return 课程介绍
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置课程介绍
     *
     * @param description 课程介绍
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取选课批次
     *
     * @return 选课批次
     */
    public int getBatch() {
        return batch;
    }

    /**
     * 设置选课批次
     *
     * @param batch 选课批次
     */
    public void setBatch(int batch) {
        this.batch = batch;
    }

    /**
     * 是否是无限人数选课
     *
     * @return 是否是无限人数选课
     * @see #getAvailableNum() 当无限人数时该值为0
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    /**
     * 设置是否是无限人数选课
     *
     * @param infinite 是否是无限人数选课
     */
    public void setInfinite(boolean infinite) {
        isInfinite = infinite;
    }

    /**
     * 获取该课程可选人数
     *
     * @return 该课程可选人数
     */
    public int getAvailableNum() {
        return availableNum;
    }

    /**
     * 设置该课程可选人数
     *
     * @param availableNum 该课程可选人数
     */
    public void setAvailableNum(int availableNum) {
        this.availableNum = availableNum;
    }

    /**
     * 获取该课程已选人数
     *
     * @return 该课程已选人数
     */
    public int getSelectedNum() {
        return selectedNum;
    }

    /**
     * 设置该课程已选人数
     *
     * @param selectedNum 该课程已选人数
     */
    public void setSelectedNum(int selectedNum) {
        this.selectedNum = selectedNum;
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
     * 是否是可选课程
     *
     * @return 是否是可选课程
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * 设置是否是可选课程
     *
     * @param available 是否是可选课程
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCourseProperty() {
        return courseProperty;
    }

    public void setCourseProperty(String courseProperty) {
        this.courseProperty = courseProperty;
    }
}
