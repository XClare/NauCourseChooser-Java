package lib.naucourse.chooser.net;

import lib.naucourse.chooser.net.school.SchoolClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

abstract class CourseSubmit {
    final SchoolClient schoolClient;
    final ReentrantLock submitLock = new ReentrantLock();
    ExecutorService pool;
    int submitResultGetCount = 0;
    boolean stopSubmit = false;

    CourseSubmit(SchoolClient schoolClient) {
        this.schoolClient = schoolClient;
        this.pool = Executors.newCachedThreadPool();
    }

    /**
     * 停止提交课程
     */
    public void stopSubmit() {
        stopSubmit = true;
    }

    /**
     * 停止提交并重置线程池
     */
    public synchronized void cleanSubmitThread() {
        stopSubmit();
        try {
            if (!pool.isShutdown()) {
                pool.shutdownNow();
            }
            pool = Executors.newCachedThreadPool();
        } finally {
            submitLock.unlock();
        }
    }
}
