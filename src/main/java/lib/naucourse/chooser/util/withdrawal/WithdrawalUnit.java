package lib.naucourse.chooser.util.withdrawal;

import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.SelectedCourse;
import okhttp3.FormBody;

public class WithdrawalUnit {
    private final SelectedCourse selectedCourse;
    private final CourseType courseType;
    private final FormBody formBody;
    private final String url;

    /**
     * 退选课程的单元
     *
     * @param selectedCourse 退选课程
     * @param courseType     课程类别
     * @param formBody       提交表单
     * @param url            提交地址
     */
    public WithdrawalUnit(SelectedCourse selectedCourse, CourseType courseType, FormBody formBody, String url) {
        this.selectedCourse = selectedCourse;
        this.courseType = courseType;
        this.formBody = formBody;
        this.url = url;
    }

    public SelectedCourse getSelectedCourse() {
        return selectedCourse;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public FormBody getFormBody() {
        return formBody;
    }

    public String getUrl() {
        return url;
    }
}
