package lib.naucourse.chooser.net.callable;

import lib.naucourse.chooser.net.CourseChoose;
import lib.naucourse.chooser.net.school.SchoolClient;
import lib.naucourse.chooser.util.choose.ChooseInfo;
import lib.naucourse.chooser.util.choose.ChooseResult;
import lib.naucourse.chooser.util.choose.ChooseUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static lib.naucourse.chooser.net.CourseChoose.CourseError.FULL_SELECTED;

public class ChooseTypeCallable implements Callable<Void> {
    private final List<ChooseUnit> mainList;
    private final List<ChooseUnit> subList;
    private final SchoolClient schoolClient;
    private final ExecutorService pool;
    private final int submitCount;
    private final boolean autoRemoveMode;
    private final boolean scanCourseMode;
    private final int maxListSubmit;
    private CourseChoose.OnSubmitListener onSubmitListener;
    private int turn;

    /**
     * 提交一个课程类别的所有课程
     *
     * @param pool         线程池
     * @param schoolClient 教务客户端
     * @param submitInfo   提交配置
     * @param mainList     主要的提交的课程
     * @param subList      次要的提交的课程
     */
    public ChooseTypeCallable(ExecutorService pool, SchoolClient schoolClient, ChooseInfo submitInfo, List<ChooseUnit> mainList, List<ChooseUnit> subList) {
        this.schoolClient = schoolClient;
        this.mainList = mainList;
        this.subList = subList;
        this.pool = pool;
        this.submitCount = submitInfo.getPerClassSubmitCount();
        this.autoRemoveMode = submitInfo.isAutoRemoveMode();
        this.scanCourseMode = submitInfo.isScanCourseMode();
        this.maxListSubmit = submitInfo.getSubmitListMaxNum();
    }

    private static void removeUselessCourse(List<ChooseUnit> submitUnitList, ChooseResult submitResult, boolean isScanCourseMode) {
        if (submitResult.isSuccess() || !checkReSubmit(submitResult, isScanCourseMode)) {
            //移除已经成功的课程提交
            submitResult.setWillAutoRemove(true);
            for (ChooseUnit submitUnit : submitUnitList) {
                if (submitUnit.getCourse() == submitResult.getCourse()) {
                    submitUnitList.remove(submitUnit);
                    break;
                }
            }
        }
    }

    private static boolean checkReSubmit(ChooseResult submitResult, boolean isScanCourseMode) {
        if (isScanCourseMode && submitResult.getErrorCode() == FULL_SELECTED) {
            return true;
        }
        switch (submitResult.getErrorCode()) {
            case UNKNOWN:
            case SYSTEM_REQUEST:
            case TIME_OUT:
            case NOT_IN_TIME:
                return true;
            default:
                return false;
        }
    }

    public void setCallBack(final int turn, final CourseChoose.OnSubmitListener onSubmitListener) {
        this.turn = turn;
        this.onSubmitListener = onSubmitListener;
    }

    @Override
    public Void call() throws Exception {
        ArrayList<Future<ChooseResult>> mainSubmitList = new ArrayList<>(mainList.size());
        ArrayList<Future<ChooseResult>> subSubmitList = new ArrayList<>(subList.size());

        int successCount = 0;
        int listSubmitCount = 0;
        int totalMainSubmitCount = mainList.size();
        //尝试所有课程直到列表内无可选课程
        while (mainList.size() != 0 && (scanCourseMode || listSubmitCount < maxListSubmit)) {
            //提交主要的课程
            for (ChooseUnit submitUnit : mainList) {
                for (int i = 0; i < submitCount; i++) {
                    mainSubmitList.add(pool.submit(new ChooseUnitCallable(schoolClient, submitUnit)));
                    if (scanCourseMode) {
                        break;
                    }
                }
            }

            //等待主要课程提交成功
            for (Future<ChooseResult> future : mainSubmitList) {
                ChooseResult submitResult = future.get();
                if (submitResult.isSuccess()) {
                    successCount++;
                }
                if (autoRemoveMode) {
                    removeUselessCourse(mainList, submitResult, scanCourseMode);
                }
                onSubmitListener.onSubmitResult(turn, submitResult);
            }
            mainSubmitList.clear();
            if (autoRemoveMode) {
                break;
            }
            listSubmitCount++;
        }
        if (submitCount != 0 && successCount == totalMainSubmitCount) {
            return null;
        }

        listSubmitCount = 0;
        //尝试所有课程直到列表内无可选课程
        while (subList.size() != 0 && (scanCourseMode || listSubmitCount < maxListSubmit)) {
            //提交次要的课程
            for (ChooseUnit submitUnit : subList) {
                for (int i = 0; i < submitCount; i++) {
                    subSubmitList.add(pool.submit(new ChooseUnitCallable(schoolClient, submitUnit)));
                    if (scanCourseMode) {
                        break;
                    }
                }
            }

            //等待次要课程提交成功
            for (Future<ChooseResult> future : subSubmitList) {
                ChooseResult submitResult = future.get();
                if (autoRemoveMode) {
                    removeUselessCourse(subList, submitResult, scanCourseMode);
                }
                onSubmitListener.onSubmitResult(turn, submitResult);
            }
            subSubmitList.clear();
            if (autoRemoveMode) {
                break;
            }
            listSubmitCount++;
        }
        return null;
    }
}