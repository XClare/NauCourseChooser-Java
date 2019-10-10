package lib.naucourse.chooser.util;

import java.io.Serializable;

public class CourseType implements Serializable {
    private String name;
    private String url;
    private boolean hasDetail;
    private int batch;
    private String startDate;
    private String endDate;
    private int availableNum;
    private int selectedNum;
    private String submitType;
    // 必须填写
    private String startDateCode;
    private String endDateCode;
    private String term;
    private String courseSelectStyle;
    private String banRule;
    private int batchAvailableNum;

    /**
     * 课程类别
     */
    public CourseType() {
        this.hasDetail = false;
    }

    /**
     * 获取课程类别名称
     *
     * @return 课程类别名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置课程类别名称
     *
     * @param name 课程类别名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取课程类别网址
     *
     * @return 课程类别网址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置课程类别网址
     *
     * @param url 课程类别网址
     */
    public void setUrl(String url) {
        this.url = url;
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
     * 获取选课开始时间字符串
     *
     * @return 选课开始时间字符串
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * 设置选课开始时间字符串
     *
     * @param startDate 选课开始时间字符串
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取选课结束时间字符串
     *
     * @return 选课结束时间字符串
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * 设置选课结束时间字符串
     *
     * @param endDate 选课结束时间字符串
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * 获取该批次可选课程数
     *
     * @return 该批次可选课程数
     */
    public int getBatchAvailableNum() {
        return batchAvailableNum;
    }

    /**
     * 设置该批次可选课程数
     *
     * @param batchAvailableNum 该批次可选课程数
     */
    public void setBatchAvailableNum(int batchAvailableNum) {
        this.batchAvailableNum = batchAvailableNum;
    }

    /**
     * 获取所有批次可选课程总数
     *
     * @return 所有批次可选课程总数
     */
    public int getAvailableNum() {
        return availableNum;
    }

    /**
     * 设置所有批次可选课程总数
     *
     * @param availableNum 所有批次可选课程总数
     */
    public void setAvailableNum(int availableNum) {
        this.availableNum = availableNum;
    }

    /**
     * 获取该类别已选课程数量
     *
     * @return 该类别已选课程数量
     */
    public int getSelectedNum() {
        return selectedNum;
    }

    /**
     * 设置该类别已选课程数量
     *
     * @param selectedNum 该类别已选课程数量
     */
    public void setSelectedNum(int selectedNum) {
        this.selectedNum = selectedNum;
    }

    /**
     * 是否包含详细类别信息
     * 第一次获取到的课程类别只包含简单的数据，请求课程列表回调中返回的才包含所有数据
     *
     * @return 是否包含详细类别信息
     * @see #getUrl()
     * @see #getName()
     */
    public boolean isHasDetail() {
        return hasDetail;
    }

    /**
     * 设置是否包含详细类别信息
     *
     * @param hasDetail 是否包含详细类别信息
     */
    public void setHasDetail(boolean hasDetail) {
        this.hasDetail = hasDetail;
    }

    /**
     * 获取提交方式
     *
     * @return 提交方式
     */
    public String getSubmitType() {
        return submitType;
    }

    /**
     * 设置提交方式
     *
     * @param submitType 提交方式
     */
    public void setSubmitType(String submitType) {
        this.submitType = submitType;
    }

    /**
     * 获取用于提交的课程选课时间开始字符串
     *
     * @return 用于提交的课程选课时间开始字符串
     */
    public String getStartDateCode() {
        return startDateCode;
    }

    /**
     * 设置用于提交的课程选课时间开始字符串
     *
     * @param startDateCode 用于提交的课程选课时间开始字符串
     */
    public void setStartDateCode(String startDateCode) {
        this.startDateCode = startDateCode;
    }

    /**
     * 获取用于提交的课程选课时间结束字符串
     *
     * @return 用于提交的课程选课时间结束字符串
     */
    public String getEndDateCode() {
        return endDateCode;
    }

    /**
     * 设置用于提交的课程选课时间结束字符串
     *
     * @param endDateCode 用于提交的课程选课时间结束字符串
     */
    public void setEndDateCode(String endDateCode) {
        this.endDateCode = endDateCode;
    }

    /**
     * 获取学期
     *
     * @return 学期
     */
    public String getTerm() {
        return term;
    }

    /**
     * 设置学期
     *
     * @param term 学期
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * 获取该课程选课方式
     * 先到先得，志愿筛选等
     *
     * @return 选课方式
     */
    public String getCourseSelectStyle() {
        return courseSelectStyle;
    }

    /**
     * 设置选课方式
     *
     * @param courseSelectStyle 选课方式
     */
    public void setCourseSelectStyle(String courseSelectStyle) {
        this.courseSelectStyle = courseSelectStyle;
    }

    /**
     * 获取BanRule
     * 该值仅用于提交时使用
     *
     * @return BanRule
     */
    public String getBanRule() {
        return banRule;
    }

    /**
     * 设置BanRule
     *
     * @param banRule BanRule
     */
    public void setBanRule(String banRule) {
        this.banRule = banRule;
    }
}
