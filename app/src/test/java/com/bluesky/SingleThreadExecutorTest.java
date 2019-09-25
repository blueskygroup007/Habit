package com.bluesky;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author BlueSky
 * @date 2019/9/9
 * Description:
 */
public class SingleThreadExecutorTest {
    static int j = 0;

    public static void main(String[] args) {
        Executor pool = Executors.newSingleThreadExecutor();
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                j++;
                System.out.println("Thread:" + Thread.currentThread().getName() + "   " + j + "");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        pool.execute(thread);
        pool.execute(thread);

        pool.execute(thread);
        pool.execute(thread);
        pool.execute(thread);

    }
}
