package lib.naucourse.chooser;

import lib.naucourse.chooser.manage.CourseManage;
import lib.naucourse.chooser.manage.SchoolClientManage;
import lib.naucourse.chooser.net.CourseChoose;
import lib.naucourse.chooser.net.CourseList;
import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.SelectedCourse;
import lib.naucourse.chooser.util.choose.ChooseInfo;
import lib.naucourse.chooser.util.choose.ChooseResult;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static String id;
    private static String pw;

    public static void main(String[] args) {
        id = "";
        pw = "";

        if (id.isEmpty() || pw.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("UserID:");
            id = scanner.nextLine();
            System.out.println("UserPW:");
            pw = scanner.nextLine();
        }
        System.out.println(id + " " + pw + " Login Start");
        showJwc(id, pw);
    }

    private static void showJwc(String userId, String userPw) {
        JarDataPath dataPath = new JarDataPath();
        SchoolClientManage jwcManage = new SchoolClientManage(dataPath, 5, 5, 5);
        jwcManage.login(userId, userPw, true, new SchoolClientManage.OnLoginListener() {
            @Override
            public boolean onLogin(String userUrl) {
                jwcManage.getCourseType(new CourseList.OnCourseTypeListener() {
                    @Override
                    public void getCourseType(ArrayList<CourseType> courseTypes) {
                        System.out.println("All Support Type:");
                        for (CourseType courseType : courseTypes) {
                            System.out.println(courseType.getName());
                        }
                        jwcManage.getCourseList(courseTypes.get(2), new CourseList.OnCourseListListener() {
                            @Override
                            public void getCourseList(CourseType courseType, ArrayList<Course> courseList, ArrayList<SelectedCourse> courseSelectedList) {
                                System.out.println("\nSelected:");
                                for (SelectedCourse selectedCourse : courseSelectedList) {
                                    System.out.println(selectedCourse.getTeachingClass() + "-" + selectedCourse.getName());
                                }
                                System.out.println("\nAll:");
                                for (Course course : courseList) {
                                    System.out.println(course.getTeachingClass() + "-" + course.getName());
                                }

                                CourseManage chooseCourseManage = new CourseManage();
                                chooseCourseManage.addChooseCourse(courseTypes.get(7), courseList.get(1), false);

                                jwcManage.submitChooseCourse(new ChooseInfo(1, 2, 10, true, false),
                                        chooseCourseManage.getChooseCourseList(),
                                        new CourseChoose.OnSubmitListener() {
                                            @Override
                                            public void onSubmitResult(int turn, ChooseResult chooseResult) {
                                                System.out.println("Turn" + turn + " " + chooseResult.getCourseType().getName()
                                                        + "-" + chooseResult.getCourse().getName() + "-" + chooseResult.getCourse().getTeachingClass()
                                                        + " Success:" + chooseResult.isSuccess() + " Error:" + chooseResult.getErrorCode() + " SubCourse:" + chooseResult.isSubCourse());
                                                System.out.println(chooseResult.getResultMsg());
                                            }

                                            @Override
                                            public void onSubmitFinish() {
                                                jwcManage.logout(null);
                                                System.out.println("All Result Get");
                                            }

                                            @Override
                                            public void onSubmitProcess(int nowTurn, int totalTurn) {
                                                System.out.println("Process: " + nowTurn + "/" + totalTurn);
                                            }

                                            @Override
                                            public void onFailed(int turn, CourseChoose.CourseError errorCode) {
                                                System.out.println("Turn" + turn + " Error: " + errorCode);
                                            }
                                        });
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
                return false;
            }

            @Override
            public void onFailed() {

            }
        });
    }
}
