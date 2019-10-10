package lib.naucourse.chooser.util.choose;

public class ChooseInfo {
    private final int perClassSubmitCount;
    private final int totalSubmitTurn;
    private final int submitListMaxNum;
    private final boolean autoRemoveMode;
    private final boolean scanCourseMode;

    /**
     * 设置提交课程的配置
     * 扫课模式下除非能够选到课程否则不会自动停止
     *
     * @param perClassSubmitCount 每个课程提交的次数（扫课模式下无效，只进行一次提交）
     * @param totalSubmitTurn     总共提交几轮（自动移除模式与扫课模式下无效，只进行一轮）
     * @param submitListMaxNum    每个课程列表最大提交次数（扫课模式下无效，主课程与被选课程列表分开计算）
     * @param autoRemoveMode      自动移除不可选课程的模式（会自动尝试直到选课成功或者无课可选）
     * @param scanCourseMode      扫课模式（会开启自动移除模式同时不会移除已经选满的课程）
     */
    public ChooseInfo(int perClassSubmitCount, int totalSubmitTurn, int submitListMaxNum, boolean autoRemoveMode, boolean scanCourseMode) {
        this.perClassSubmitCount = perClassSubmitCount;
        this.totalSubmitTurn = totalSubmitTurn;
        this.submitListMaxNum = submitListMaxNum;
        this.scanCourseMode = scanCourseMode;
        if (scanCourseMode) {
            this.autoRemoveMode = true;
        } else {
            this.autoRemoveMode = autoRemoveMode;
        }
    }

    public int getPerClassSubmitCount() {
        return perClassSubmitCount;
    }

    public int getTotalSubmitTurn() {
        return totalSubmitTurn;
    }

    public boolean isAutoRemoveMode() {
        return autoRemoveMode;
    }

    public boolean isScanCourseMode() {
        return scanCourseMode;
    }

    public int getSubmitListMaxNum() {
        return submitListMaxNum;
    }
}
