package lib.naucourse.chooser.net.callable;

import lib.naucourse.chooser.net.CourseChoose;
import lib.naucourse.chooser.net.school.SchoolClient;
import lib.naucourse.chooser.util.choose.ChooseResult;
import lib.naucourse.chooser.util.choose.ChooseUnit;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

import static lib.naucourse.chooser.net.CourseChoose.CourseError.*;

class ChooseUnitCallable implements Callable<ChooseResult> {
    private final SchoolClient schoolClient;
    private final ChooseUnit submitUnit;

    /**
     * 提交一个课程
     *
     * @param schoolClient 教务客户端
     * @param submitUnit   选课提交单元
     */
    ChooseUnitCallable(SchoolClient schoolClient, ChooseUnit submitUnit) {
        this.schoolClient = schoolClient;
        this.submitUnit = submitUnit;
    }

    /**
     * 获取错误代码
     *
     * @param htmlContent 返回的内容
     * @return 错误代码
     */
    private static CourseChoose.CourseError getErrorCode(String htmlContent) {
        if (htmlContent.contains("系统错误提示页")) {
            return SYSTEM_REQUEST;
        } else if (htmlContent.contains("系统不在开放时间内")) {
            return NOT_IN_TIME;
        } else if (htmlContent.contains("已经选修")) {
            return HAS_SELECTED;
        } else if (htmlContent.contains("同名课程")) {
            return HAS_STUDIED_SAME_COURSE;
        } else if (htmlContent.contains("超出当前批次")) {
            return SELECT_OUT_OF_LIMIT;
        } else if (htmlContent.contains("已经选满")) {
            return ENOUGH_SELECTED;
        } else if (htmlContent.contains("人数已满")) {
            return FULL_SELECTED;
        } else if (htmlContent.contains("超过最大学分")) {
            return MAX_SCORE;
        } else if (htmlContent.contains("安排相冲突")) {
            return CONFLICT_WITH_TABLE_COURSE;
        } else if (htmlContent.contains("时间相冲突")) {
            return CONFLICT_WITH_SELECTED_COURSE;
        } else if (htmlContent.contains("不得选修")) {
            return NOT_ABLE_TO_CHOOSE;
        } else {
            return UNKNOWN;
        }
    }

    @Override
    public ChooseResult call() {
        ChooseResult result;
        try {
            String content = schoolClient.postJwcData(submitUnit.getUrl(), submitUnit.getFormBody());
            result = new ChooseResult(submitUnit.getCourseType(), submitUnit.getCourse(), content, getErrorCode(content), content.contains("添加成功"), submitUnit.isSubCourse());
        } catch (SocketTimeoutException e) {
            result = new ChooseResult(submitUnit.getCourseType(), submitUnit.getCourse(), null, TIME_OUT, false, submitUnit.isSubCourse());
        } catch (IOException e) {
            result = new ChooseResult(submitUnit.getCourseType(), submitUnit.getCourse(), null, DATA_POST, false, submitUnit.isSubCourse());
        }
        return result;
    }
}