package lib.naucourse.chooser.net.coursetype;

import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.SelectedCourse;
import org.jsoup.nodes.Element;

/**
 * 默认课程类别分析器
 */
public class DefaultAnalyseCourseType implements AnalyseCourseType {
    @Override
    public String getCourseType() {
        return null;
    }

    @Override
    public boolean analyseCourseList(Course course, int tdIndex, Element tag, String text) {
        switch (tdIndex) {
            case 0:
                if (text.contains("禁选")) {
                    text = text.replace("禁选", "");
                    course.setAvailable(false);
                }
                course.setIndex(Integer.parseInt(text));
                for (Element element : tag.getElementsByTag("input")) {
                    if (element.hasAttr("value")) {
                        course.setCourseId(element.attr("value"));
                    }
                }
                break;
            case 1:
                course.setTeachingClass(text);
                break;
            case 2:
                course.setName(text);
                break;
            case 3:
                if (text.isEmpty()) {
                    course.setScore(0);
                } else {
                    course.setScore(Float.parseFloat(text));
                }
                break;
            case 4:
                course.setCollege(text);
                break;
            case 5:
                course.setTeacher(text);
                break;
            case 6:
                course.setTime(text);
                break;
            case 7:
                course.setDescription(text);
                break;
            case 8:
                course.setBatch(Integer.parseInt(text.substring(1)));
                break;
            case 9:
                if (text.contains("不限")) {
                    course.setAvailableNum(0);
                    course.setInfinite(true);
                } else {
                    course.setAvailableNum(Integer.parseInt(text));
                    course.setInfinite(false);
                }
                break;
            case 10:
                course.setSelectedNum(Integer.parseInt(text));
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean analyseSelectedCourse(SelectedCourse selectedCourse, int tdIndex, Element tag, String text) {
        switch (tdIndex) {
            case 0:
                selectedCourse.setIndex(Integer.parseInt(text));
                break;
            case 1:
                selectedCourse.setTeachingClass(text);
                break;
            case 2:
                selectedCourse.setName(text);
                for (Element element : tag.getElementsByTag("a")) {
                    if (element.hasAttr("href") && element.attr("href").length() > 6) {
                        selectedCourse.setCourseId(element.attr("href").substring(6));
                    }
                }
                break;
            case 3:
                if (text.isEmpty()) {
                    selectedCourse.setScore(0);
                } else {
                    selectedCourse.setScore(Float.parseFloat(text));
                }
                break;
            case 4:
                selectedCourse.setCollege(text);
                break;
            case 5:
                selectedCourse.setTeacher(text);
                break;
            case 6:
                selectedCourse.setSelectTime(text);
                break;
            case 7:
                for (Element element : tag.getElementsByTag("a")) {
                    if (element.hasAttr("href")) {
                        String href = element.attr("href");
                        if (href.length() > 25) {
                            String postData = href.substring(23, href.length() - 2);
                            if (postData.contains("','")) {
                                String[] postArr = postData.split("','");
                                for (int k = 0; k < postArr.length; k++) {
                                    switch (k) {
                                        case 0:
                                            selectedCourse.setPostIndex(Integer.parseInt(postArr[k]));
                                            break;
                                        case 1:
                                            selectedCourse.setPostId(postArr[k]);
                                            break;
                                        case 2:
                                            selectedCourse.setPostTc(postArr[k]);
                                            break;
                                        case 3:
                                            selectedCourse.setPostc(postArr[k]);
                                            break;
                                        case 4:
                                            selectedCourse.setPostTm(postArr[k]);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                return false;
        }
        return true;
    }
}
