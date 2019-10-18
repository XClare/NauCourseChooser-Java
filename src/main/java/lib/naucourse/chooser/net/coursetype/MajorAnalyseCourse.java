package lib.naucourse.chooser.net.coursetype;

import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.SelectedCourse;
import org.jsoup.nodes.Element;

/**
 * 专业选修课课程类别分析器
 */
public class MajorAnalyseCourse implements AnalyseCourseType {
    @Override
    public String getCourseType() {
        return "专业选修课";
    }

    @Override
    public boolean analyseCourseList(Course course, int tdIndex, Element tag, String text) {
        switch (tdIndex) {
            case 4:
                course.setTeacher(text);
                break;
            case 5:
                course.setTime(text);
                break;
            case 6:
                break;
            case 7:
                if (text.contains("不限")) {
                    course.setAvailableNum(0);
                    course.setInfinite(true);
                } else {
                    course.setAvailableNum(Integer.parseInt(text));
                    course.setInfinite(false);
                }
                break;
            case 8:
                course.setSelectedNum(Integer.parseInt(text));
                break;
            case 9:
                course.setBatch(Integer.parseInt(text.substring(1)));
                break;
            case 10:
                course.setDescription(text);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean analyseSelectedCourse(SelectedCourse selectedCourse, int tdIndex, Element tag, String text) {
        switch (tdIndex) {
            case 4:
                selectedCourse.setTeacher(text);
                break;
            case 5:
                selectedCourse.setSelectTime(text);
                break;
            case 6:
                selectedCourse.setCourseType(text);
                break;
            default:
                return false;
        }
        return true;
    }
}