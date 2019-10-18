package lib.naucourse.chooser.net.coursetype;

import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.SelectedCourse;
import org.jsoup.nodes.Element;

/**
 * 学校安排重修课程类别分析器
 */
public class SchoolRetakeAnalyseCourse implements AnalyseCourseType {
    @Override
    public String getCourseType() {
        return "学校安排重修";
    }

    @Override
    public boolean analyseCourseList(Course course, int tdIndex, Element tag, String text) {
        switch (tdIndex) {
            case 6:
                course.setCourseProperty(text);
                break;
            case 8:
                if (text.contains("不限")) {
                    course.setAvailableNum(0);
                    course.setInfinite(true);
                } else {
                    course.setAvailableNum(Integer.parseInt(text));
                    course.setInfinite(false);
                }
                break;
            case 9:
                course.setSelectedNum(Integer.parseInt(text));
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean analyseSelectedCourse(SelectedCourse selectedCourse, int tdIndex, Element tag, String text) {
        return false;
    }
}