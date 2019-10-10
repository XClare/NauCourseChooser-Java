package lib.naucourse.chooser.manage;

import lib.naucourse.chooser.net.CourseChoose;
import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.SelectedCourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CourseManage {
    private final LinkedHashMap<CourseType, HashMap<String, ArrayList<Course>>> chooseCourseList;
    private final LinkedHashMap<CourseType, ArrayList<SelectedCourse>> withdrawalCourseList;

    /**
     * 用于选课管理
     */
    public CourseManage() {
        this.chooseCourseList = new LinkedHashMap<>();
        this.withdrawalCourseList = new LinkedHashMap<>();
    }

    /**
     * 选课管理
     *
     * @param chooseCourseList     选择的课程
     * @param withdrawalCourseList 退选课程
     */
    public CourseManage(LinkedHashMap<CourseType, HashMap<String, ArrayList<Course>>> chooseCourseList, LinkedHashMap<CourseType, ArrayList<SelectedCourse>> withdrawalCourseList) {
        this.chooseCourseList = chooseCourseList;
        this.withdrawalCourseList = withdrawalCourseList;
    }

    /**
     * 获取选择的课程
     *
     * @return 选择的课程
     */
    public LinkedHashMap<CourseType, HashMap<String, ArrayList<Course>>> getChooseCourseList() {
        return this.chooseCourseList;
    }

    /**
     * 获取退选的课程
     *
     * @return 退选的课程
     */
    public LinkedHashMap<CourseType, ArrayList<SelectedCourse>> getWithdrawalCourseList() {
        return this.withdrawalCourseList;
    }

    /**
     * 添加准备选的课程
     * 同时会检查课程是否重复
     *
     * @param courseType  课程类别
     * @param course      课程
     * @param isSubCourse 是否是被选课程
     * @return 是否添加成功
     */
    synchronized public boolean addChooseCourse(CourseType courseType, Course course, boolean isSubCourse) {
        boolean foundCourseType = false;
        String chooseSubmitType;
        if (isSubCourse) {
            chooseSubmitType = CourseChoose.CHOOSE_SUBMIT_TYPE_SUB;
        } else {
            chooseSubmitType = CourseChoose.CHOOSE_SUBMIT_TYPE_MAIN;
        }

        for (CourseType type : chooseCourseList.keySet()) {
            if (type.getName() != null && type.getName().equals(courseType.getName())) {
                foundCourseType = true;
                HashMap<String, ArrayList<Course>> courseHashMap = chooseCourseList.get(type);

                boolean foundChooseType = false;
                for (String chooseType : courseHashMap.keySet()) {
                    if (chooseSubmitType.equals(chooseType)) {
                        foundChooseType = true;
                        ArrayList<Course> courseList = courseHashMap.get(chooseType);
                        //主选课列表超出最大选课数量
                        if (!isSubCourse && courseList.size() >= courseType.getBatchAvailableNum()) {
                            return false;
                        }

                        for (Course cCourse : courseList) {
                            if (cCourse != null && cCourse.getCourseId() != null && cCourse.getCourseId().equals(course.getCourseId()) && cCourse.getTeachingClass() != null && cCourse.getTeachingClass().equals(course.getTeachingClass())) {
                                return false;
                            }
                        }

                        courseList.add(course);
                        break;
                    }
                }

                if (!foundChooseType) {
                    ArrayList<Course> courseList = new ArrayList<>();
                    courseList.add(course);
                    courseHashMap.put(chooseSubmitType, courseList);
                }
                break;
            }
        }
        if (!foundCourseType) {
            HashMap<String, ArrayList<Course>> courseHashMap = new HashMap<>();
            ArrayList<Course> courseList = new ArrayList<>();
            courseList.add(course);
            courseHashMap.put(chooseSubmitType, courseList);
            chooseCourseList.put(courseType, courseHashMap);
        }
        return true;
    }

    /**
     * 删除准备选的课程
     *
     * @param courseType  课程类别
     * @param course      课程
     * @param isSubCourse 是否是被选课程
     * @return 是否删除成功
     */
    synchronized public boolean removeChooseCourse(CourseType courseType, Course course, boolean isSubCourse) {
        String chooseSubmitType;
        if (isSubCourse) {
            chooseSubmitType = CourseChoose.CHOOSE_SUBMIT_TYPE_SUB;
        } else {
            chooseSubmitType = CourseChoose.CHOOSE_SUBMIT_TYPE_MAIN;
        }
        for (CourseType type : chooseCourseList.keySet()) {
            if (type.getName() != null && type.getName().equals(courseType.getName())) {
                HashMap<String, ArrayList<Course>> courseHashMap = chooseCourseList.get(type);

                for (String chooseType : courseHashMap.keySet()) {
                    if (chooseSubmitType.equals(chooseType)) {
                        ArrayList<Course> courseList = courseHashMap.get(chooseType);

                        for (Course cCourse : courseList) {
                            if (cCourse != null && cCourse.getIndex() == course.getIndex() && cCourse.getTeachingClass() != null && cCourse.getTeachingClass().equals(course.getTeachingClass())) {
                                courseList.remove(cCourse);
                                return true;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return false;
    }

    /**
     * 添加准备退选课程
     * 同时会检查课程是否重复
     *
     * @param courseType     课程类别
     * @param selectedCourse 已选的课
     * @return 是否添加成功
     */
    synchronized public boolean addWithdrawalCourse(CourseType courseType, SelectedCourse selectedCourse) {
        boolean foundCourseType = false;
        for (CourseType type : withdrawalCourseList.keySet()) {
            if (type.getName() != null && type.getName().equals(courseType.getName())) {
                foundCourseType = true;
                ArrayList<SelectedCourse> selectedCourses = withdrawalCourseList.get(type);
                for (SelectedCourse course : selectedCourses) {
                    if (course != null && course.getCourseId() != null && course.getCourseId().equals(selectedCourse.getCourseId()) && course.getTeachingClass() != null && course.getTeachingClass().equals(selectedCourse.getTeachingClass())) {
                        return false;
                    }
                }
                selectedCourses.add(selectedCourse);
                break;
            }
        }
        if (!foundCourseType) {
            ArrayList<SelectedCourse> selectedCourses = new ArrayList<>();
            selectedCourses.add(selectedCourse);
            withdrawalCourseList.put(courseType, selectedCourses);
        }
        return true;
    }

    /**
     * 移除准备退选的课程
     *
     * @param courseType     课程类别
     * @param selectedCourse 已选的课
     * @return 是否移除成功
     */
    synchronized public boolean removeWithdrawalCourse(CourseType courseType, SelectedCourse selectedCourse) {
        for (CourseType type : withdrawalCourseList.keySet()) {
            if (type.getName() != null && type.getName().equals(courseType.getName())) {
                ArrayList<SelectedCourse> selectedCourses = withdrawalCourseList.get(type);
                for (SelectedCourse course : selectedCourses) {
                    if (course != null && course.getCourseId() != null && course.getCourseId().equals(selectedCourse.getCourseId()) && course.getTeachingClass() != null && course.getTeachingClass().equals(selectedCourse.getTeachingClass())) {
                        selectedCourses.remove(course);
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
}
