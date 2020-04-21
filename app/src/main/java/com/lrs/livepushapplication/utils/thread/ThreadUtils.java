package com.lrs.livepushapplication.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {
    //线程池容器
    public static ExecutorService poolExecutor;
    //线程池总数
    private static int THREADNUMS = 5;
    /**
     * 实例线程池
     * @return
     */
    private static ExecutorService getThread() {
        if (poolExecutor == null) {
            poolExecutor = Executors.newFixedThreadPool(THREADNUMS);
        }
        return poolExecutor;
    }

    /**
     *启动线程
     * @param runnable
     */
    public static void runThread(Runnable runnable) {
        if (poolExecutor == null) {
            getThread();
        }
        poolExecutor.execute(runnable);
    }
}
