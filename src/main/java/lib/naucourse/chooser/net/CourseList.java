package lib.naucourse.chooser.net;

import lib.naucourse.chooser.net.school.SchoolClient;
import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.SelectedCourse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CourseList {
    /**
     * 普通提交方式
     */
    static final String SUBMIT_TYPE_NORMAL = "Normal";
    /**
     * RepairGroup提交方式
     */
    static final String SUBMIT_TYPE_REPAIR_GROUP = "RepairGroup_Submit";
    /**
     * 英语后续课程提交方式
     */
    static final String SUBMIT_TYPE_ENG_EXPAND = "EngExpandSubmit";
    /**
     * 专业选修课提交方式
     */
    static final String SUBMIT_TYPE_ZX = "ZX_Submit";

    private final SchoolClient schoolClient;

    /**
     * 课程列表获取
     *
     * @param schoolClient 教务客户端
     */
    public CourseList(SchoolClient schoolClient) {
        this.schoolClient = schoolClient;
    }

    /**
     * 从html获取课程类型列表
     *
     * @param html 网页数据
     * @return 课程类别列表
     */
    private static ArrayList<CourseType> getCourseTypeListFromHtml(String html) {
        ArrayList<CourseType> courseTypeList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        if (document != null) {
            Element ul = document.body().getElementById("tt");
            Elements span = ul.getElementsByTag("span");
            boolean listStart = false;
            for (Element element : span) {
                if (element.hasText()) {
                    String text = element.text().trim();
                    if ("在线选课".equals(text)) {
                        listStart = true;
                    } else if ("考试报名".equals(text)) {
                        listStart = false;
                    } else if (listStart) {
                        if (!text.contains("补、退课申请及查询") && !text.contains("学位考试")) {
                            CourseType courseType = new CourseType();
                            courseType.setName(text);
                            Elements a = element.getElementsByTag("a");
                            String url = null;
                            for (Element href : a) {
                                if (href.hasAttr("href")) {
                                    url = href.attr("href");
                                    if (url != null && url.length() > 22) {
                                        url = url.substring(19, url.length() - 2);
                                        url = String.format("%sStudents/%s", SchoolClient.JWC_SERVER_URL, url);
                                        break;
                                    }
                                }
                            }
                            courseType.setUrl(url);
                            setSubmitType(courseType);
                            courseTypeList.add(courseType);
                        }
                    }
                }
            }
        }
        return courseTypeList;
    }

    /**
     * 从html获取指定类别的课程列表
     * 会将课程类别填充完整
     *
     * @param html               网页数据
     * @param courseType         课程类别
     * @param courseList         课程列表
     * @param courseSelectedList 已选课程列表
     */
    private static void getCourseListFromHtml(String html, CourseType courseType, ArrayList<Course> courseList, ArrayList<SelectedCourse> courseSelectedList) {
        Document document = Jsoup.parse(html);
        if (document != null) {
            //填充课程列表缺失的数据
            String startIndexStr = "选课信息：当前选课是第 <span style='text-decoration:underline;font-weight:bold;color:red;font-size:12pt;'>";
            String endIndexStr = "</span> 轮选课,选课方式：";
            if (html.contains(startIndexStr) && html.contains(endIndexStr)) {
                String batch = html.substring(html.indexOf(startIndexStr) + startIndexStr.length(), html.indexOf(endIndexStr));
                courseType.setBatch(Integer.parseInt(batch));
            }

            startIndexStr = "$(\"#Term\").text('";
            endIndexStr = "');";
            if (html.contains(startIndexStr) && html.contains(endIndexStr)) {
                int startIndex = html.indexOf(startIndexStr) + startIndexStr.length();
                String term = html.substring(startIndex, html.indexOf(endIndexStr, startIndex));
                courseType.setTerm(term);
            }

            Element body = document.body();
            Element s = body.getElementById("s");
            if (s != null) {
                courseType.setStartDateCode(s.text());
            }
            Element e = body.getElementById("e");
            if (e != null) {
                courseType.setEndDateCode(e.text());
            }
            Element courseSelectStyle = body.getElementById("CourseSelectStyle");
            if (courseSelectStyle != null) {
                courseType.setCourseSelectStyle(courseSelectStyle.text());
            }
            Element totalLimitInfo = body.getElementById("TotalLimitInfo");
            if (totalLimitInfo != null) {
                courseType.setAvailableNum(Integer.parseInt(totalLimitInfo.text()));
            }
            Element electedNum = body.getElementById("ElectedNum");
            if (electedNum != null) {
                courseType.setSelectedNum(Integer.parseInt(electedNum.text()));
            }
            Element limitInfo = body.getElementById("LimitInfo");
            if (limitInfo != null) {
                courseType.setBatchAvailableNum(Integer.parseInt(limitInfo.text()));
            }
            Element startDate = body.getElementById("StartDate");
            if (startDate != null) {
                courseType.setStartDate(startDate.text());
            }
            Element endDate = body.getElementById("EndDate");
            if (endDate != null) {
                courseType.setEndDate(endDate.text());
            }
            Element rule = body.getElementById("rule");
            if (rule != null) {
                courseType.setBanRule(rule.text());
            }

            courseType.setHasDetail(true);

            //已选课程列表
            Element selectedCourseList = body.getElementById("SelectedCourses");
            if (selectedCourseList != null) {
                Elements tr = selectedCourseList.getElementsByTag("tr");
                for (int i = 2; i < tr.size(); i++) {
                    SelectedCourse selectedCourse = new SelectedCourse();
                    Elements td = tr.get(i).getElementsByTag("td");
                    for (int j = 0; j < td.size(); j++) {
                        Element detail = td.get(j);
                        String text = detail.text();
                        //区分处理不同类别的课程选课数据
                        switch (j) {
                            case 0:
                                selectedCourse.setIndex(Integer.parseInt(text));
                                break;
                            case 1:
                                selectedCourse.setTeachingClass(text);
                                break;
                            case 2:
                                selectedCourse.setName(text);
                                for (Element element : detail.getElementsByTag("a")) {
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
                                if (courseType.getName().contains("专业选修课")) {
                                    selectedCourse.setTeacher(text);
                                } else {
                                    selectedCourse.setCollege(text);
                                }
                                break;
                            case 5:
                                if (courseType.getName().contains("专业选修课")) {
                                    selectedCourse.setSelectTime(text);
                                } else {
                                    selectedCourse.setTeacher(text);
                                }
                                break;
                            case 6:
                                if (courseType.getName().contains("专业选修课")) {
                                    selectedCourse.setCourseType(text);
                                } else {
                                    selectedCourse.setSelectTime(text);
                                }
                                break;
                            case 7:
                                for (Element element : detail.getElementsByTag("a")) {
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
                        }
                    }
                    courseSelectedList.add(selectedCourse);
                }
            }

            //所有选课列表
            Element courseListTable = body.getElementById("CourseList");
            if (courseListTable != null) {
                Elements tr = courseListTable.getElementsByTag("tr");
                for (int i = 2; i < tr.size(); i++) {
                    Course course = new Course();
                    Elements td = tr.get(i).getElementsByTag("td");
                    for (int j = 0; j < td.size(); j++) {
                        Element detail = td.get(j);
                        String text = detail.text();
                        //区分处理不同类别的课程选课数据
                        switch (j) {
                            case 0:
                                if (text.contains("禁选")) {
                                    text = text.replace("禁选", "");
                                    course.setAvailable(false);
                                }
                                course.setIndex(Integer.parseInt(text));
                                for (Element element : detail.getElementsByTag("input")) {
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
                                if (courseType.getName().contains("专业选修课")) {
                                    course.setTeacher(text);
                                } else {
                                    course.setCollege(text);
                                }
                                break;
                            case 5:
                                if (courseType.getName().contains("专业选修课")) {
                                    course.setTime(text);
                                } else {
                                    course.setTeacher(text);
                                }
                                break;
                            case 6:
                                if (courseType.getName().contains("学校安排重修")) {
                                    course.setCourseProperty(text);
                                } else if (!courseType.getName().contains("专业选修课")) {
                                    course.setTime(text);
                                }
                                break;
                            case 7:
                                if (courseType.getName().contains("专业选修课")) {
                                    if (text.contains("不限")) {
                                        course.setAvailableNum(0);
                                        course.setInfinite(true);
                                    } else {
                                        course.setAvailableNum(Integer.parseInt(text));
                                        course.setInfinite(false);
                                    }
                                } else {
                                    course.setDescription(text);
                                }
                                break;
                            case 8:
                                if (courseType.getName().contains("专业选修课")) {
                                    course.setSelectedNum(Integer.parseInt(text));
                                } else if (courseType.getName().contains("英语后续课程") || courseType.getName().contains("学校安排重修")) {
                                    if (text.contains("不限")) {
                                        course.setAvailableNum(0);
                                        course.setInfinite(true);
                                    } else {
                                        course.setAvailableNum(Integer.parseInt(text));
                                        course.setInfinite(false);
                                    }
                                } else {
                                    course.setBatch(Integer.parseInt(text.substring(1)));
                                }
                                break;
                            case 9:
                                if (courseType.getName().contains("专业选修课")) {
                                    course.setBatch(Integer.parseInt(text.substring(1)));
                                } else if (courseType.getName().contains("英语后续课程") || courseType.getName().contains("学校安排重修")) {
                                    course.setSelectedNum(Integer.parseInt(text));
                                } else {
                                    if (text.contains("不限")) {
                                        course.setAvailableNum(0);
                                        course.setInfinite(true);
                                    } else {
                                        course.setAvailableNum(Integer.parseInt(text));
                                        course.setInfinite(false);
                                    }
                                }
                                break;
                            case 10:
                                if (courseType.getName().contains("专业选修课")) {
                                    course.setDescription(text);
                                } else if (courseType.getName().contains("英语后续课程")) {
                                    course.setBatch(Integer.parseInt(text.substring(1)));
                                } else {
                                    course.setSelectedNum(Integer.parseInt(text));
                                }
                                break;
                        }
                    }
                    courseList.add(course);
                }
            }
        }
    }

    /**
     * 获取提交类型
     *
     * @param courseType 课程类别
     */
    private static void setSubmitType(CourseType courseType) {
        switch (courseType.getName()) {
            case "英语后续课程":
                courseType.setSubmitType(SUBMIT_TYPE_ENG_EXPAND);
                break;
            case "组班重修":
                courseType.setSubmitType(SUBMIT_TYPE_REPAIR_GROUP);
                break;
            case "专业选修课":
                courseType.setSubmitType(SUBMIT_TYPE_ZX);
                break;
            default:
                courseType.setSubmitType(SUBMIT_TYPE_NORMAL);
                break;
        }
    }

    /**
     * 获取课程类别列表
     *
     * @param onCourseTypeListener 课程类别列表监听器
     */
    public void getCourseTypeList(OnCourseTypeListener onCourseTypeListener) {
        if (onCourseTypeListener != null) {
            if (schoolClient != null) {
                String url = schoolClient.getMainPageUrl();
                if (url == null) {
                    onCourseTypeListener.onError();
                } else {
                    schoolClient.getJwcData(url, true, new SchoolClient.OnNetListener() {
                        @Override
                        public void onSuccess(String url, String html) {
                            onCourseTypeListener.getCourseType(getCourseTypeListFromHtml(html));
                        }

                        @Override
                        public void onError(SchoolClient.ClientError errorCode) {
                            onCourseTypeListener.onError();
                        }

                        @Override
                        public void onFailure(IOException e) {
                            onCourseTypeListener.onError();
                        }
                    });
                }
            } else {
                onCourseTypeListener.onError();
            }
        }
    }

    /**
     * 获取课程列表
     *
     * @param courseType           课程类别
     * @param onCourseListListener 课程列表监听器
     */
    public void getCourseList(CourseType courseType, OnCourseListListener onCourseListListener) {
        if (onCourseListListener != null) {
            if (schoolClient != null && courseType != null) {
                String url = courseType.getUrl();
                if (url == null) {
                    onCourseListListener.onError();
                } else {
                    schoolClient.getJwcData(url, true, new SchoolClient.OnNetListener() {
                        @Override
                        public void onSuccess(String url, String html) {
                            ArrayList<Course> courseList = new ArrayList<>();
                            ArrayList<SelectedCourse> courseSelectedList = new ArrayList<>();
                            getCourseListFromHtml(html, courseType, courseList, courseSelectedList);
                            onCourseListListener.getCourseList(courseType, courseList, courseSelectedList);
                        }

                        @Override
                        public void onError(SchoolClient.ClientError errorCode) {
                            onCourseListListener.onError();
                        }

                        @Override
                        public void onFailure(IOException e) {
                            onCourseListListener.onError();
                        }
                    });
                }
            } else {
                onCourseListListener.onError();
            }
        }
    }

    /**
     * 课程类别列表监听器
     */
    public interface OnCourseTypeListener {
        /**
         * 获取到数据时的回调
         *
         * @param courseTypes 课程类别
         */
        void getCourseType(ArrayList<CourseType> courseTypes);

        /**
         * 出错时的回调
         */
        void onError();
    }

    /**
     * 课程列表监听器
     */
    public interface OnCourseListListener {
        /**
         * 获取到数据时的回调
         *
         * @param courseType         课程类别（完整数据）
         * @param courseList         课程列表
         * @param courseSelectedList 已选课程
         */
        void getCourseList(CourseType courseType, ArrayList<Course> courseList, ArrayList<SelectedCourse> courseSelectedList);

        /**
         * 出错时的回调
         */
        void onError();
    }
}
