package com.bluesky.synctest;

/**
 * @author BlueSky
 * @date 2019/8/5
 * Description:
 */
public class SyncTest {
    public static Object lock = new Object();
    public static boolean condition = false;

    static class Thread1 implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "  :启动了......");

            synchronized (lock) {
                while (!condition) {
                    System.out.println(Thread.currentThread().getName() + "  :阻塞了......");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(Thread.currentThread().getName() + "  :走出阻塞了!!!!");
            }
        }
    }

    static class Thread2 implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "  :启动了......");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                condition = true;
                lock.notify();
            }
            System.out.println(Thread.currentThread().getName() + "  :解锁了!!!!");

        }
    }

    static class ThreadTimer implements Runnable {

        @Override
        public void run() {
            while (true) {
                System.out.println("......");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SyncTest test = new SyncTest();
        new Thread(new ThreadTimer(), "心跳线程").start();
        new Thread(new Thread1(), "第1个线程").start();
        new Thread(new Thread2(), "第2个线程").start();
//        new Thread(new Thread1(), "第3个线程").start();

/*        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new Thread1(), "第4个线程").start();*/


    }
}
