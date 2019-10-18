package lib.naucourse.chooser.net.coursetype;

import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.SelectedCourse;
import org.jsoup.nodes.Element;

/**
 * 英语后续课程类别分析器
 */
public class EnglishFollowAnalyseCourse implements AnalyseCourseType {
    @Override
    public String getCourseType() {
        return "英语后续课程";
    }

    @Override
    public boolean analyseCourseList(Course course, int tdIndex, Element tag, String text) {
        switch (tdIndex) {
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
            case 10:
                course.setBatch(Integer.parseInt(text.substring(1)));
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
