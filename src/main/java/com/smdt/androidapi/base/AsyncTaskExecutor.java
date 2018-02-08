package com.smdt.androidapi.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 创建线程池
 * AUTHOR: Champion Dragon
 * created at 2017/11/11
 **/
public class AsyncTaskExecutor {
    private static AsyncTaskExecutor mInstance = null;
    private static final int maxExecutor = 4;
    private ExecutorService mExecutor = null;

    public static AsyncTaskExecutor getinstance() {
        if (mInstance == null) {
            mInstance = new AsyncTaskExecutor();
        }
        return mInstance;
    }

    protected AsyncTaskExecutor() {
        mExecutor = Executors.newFixedThreadPool(maxExecutor);
    }

    public void submit(Runnable a) {
        mExecutor.execute(a);
    }
}