package com.bluesky.habit.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author BlueSky
 * @date 2019/3/23
 * Description:
 */
class DiskIOThreadExecutor implements Executor {
    private final Executor mDiskIO;

    public DiskIOThreadExecutor() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(Runnable command) {
        mDiskIO.execute(command);
    }
}
