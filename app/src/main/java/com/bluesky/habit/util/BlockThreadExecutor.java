package com.bluesky.habit.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author BlueSky
 * @date 2019/7/29
 * Description:
 */
public class BlockThreadExecutor implements Executor {
    private final Executor mBlock;

    public BlockThreadExecutor() {
        mBlock = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(Runnable command) {
        mBlock.execute(command);
    }
}
