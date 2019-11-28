package lib.naucourse.chooser.net.coursetype;

import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.SelectedCourse;
import org.jsoup.nodes.Element;

public interface AnalyseCourseType {
    /**
     * 获取课程类别
     *
     * @return 课程类型的字符串名称（为null时指所有类型，仅限默认类别分析器使用）
     */
    String getCourseType();

    /**
     * 分析已选的课程
     *
     * @param selectedCourse 已选课程对象
     * @param tdIndex        HTML 第几个 td TAG
     * @param tag            HTML元素
     * @param text           TAG中的字符串
     * @return 是否对改行有分析结果
     */
    boolean analyseSelectedCourse(SelectedCourse selectedCourse, int tdIndex, Element tag, String text);

    /**
     * 分析列表中的课程
     *
     * @param course  课程对象
     * @param tdIndex HTML 第几个 td TAG
     * @param tag     HTML元素
     * @param text    TAG中的字符串
     * @return 是否对改行有分析结果
     */
    boolean analyseCourseList(Course course, int tdIndex, Element tag, String text);
}
