package com.bluesky.habit.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author BlueSky
 * @date 2019/3/23
 * Description:
 */
public class AppExecutors {

    private static final int THREAD_COUNT = 3;
    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;
    private final Executor monitorThread;
    private final Executor blockThread;

    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread, Executor monitorThread, Executor blockThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
        this.monitorThread = monitorThread;
        this.blockThread = blockThread;
    }

    public AppExecutors() {
        this(new DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT), new MainThreadExecutor(), Executors.newCachedThreadPool(), Executors.newSingleThreadExecutor());
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getMonitorThread() {
        return monitorThread;
    }

    public Executor getBlockThread() {
        return blockThread;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
