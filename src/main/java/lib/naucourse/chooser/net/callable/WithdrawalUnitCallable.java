package lib.naucourse.chooser.net.callable;

import lib.naucourse.chooser.net.CourseWithdrawal.WithdrawalError;
import lib.naucourse.chooser.net.school.SchoolClient;
import lib.naucourse.chooser.util.withdrawal.WithdrawalResult;
import lib.naucourse.chooser.util.withdrawal.WithdrawalUnit;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

public class WithdrawalUnitCallable implements Callable<WithdrawalResult> {
    private final SchoolClient schoolClient;
    private final WithdrawalUnit withdrawalUnit;

    /**
     * 退选一个课程
     *
     * @param schoolClient   教务客户端
     * @param withdrawalUnit 退选提交单元
     */
    public WithdrawalUnitCallable(SchoolClient schoolClient, WithdrawalUnit withdrawalUnit) {
        this.schoolClient = schoolClient;
        this.withdrawalUnit = withdrawalUnit;
    }

    @Override
    public WithdrawalResult call() {
        WithdrawalResult result;
        try {
            String content = schoolClient.postJwcData(withdrawalUnit.getUrl(), withdrawalUnit.getFormBody());
            result = new WithdrawalResult(withdrawalUnit.getCourseType(), withdrawalUnit.getSelectedCourse(), content, WithdrawalError.UNKNOWN, content.contains("退课成功"));
        } catch (SocketTimeoutException e) {
            result = new WithdrawalResult(withdrawalUnit.getCourseType(), withdrawalUnit.getSelectedCourse(), null, WithdrawalError.TIME_OUT, false);
        } catch (IOException e) {
            result = new WithdrawalResult(withdrawalUnit.getCourseType(), withdrawalUnit.getSelectedCourse(), null, WithdrawalError.DATA_POST, false);
        }
        return result;
    }
}
