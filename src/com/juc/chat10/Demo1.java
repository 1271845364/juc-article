package com.juc.chat10;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/11
 */
public class Demo1 {

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
                }
            }
        });

        thread.setName("t1");
        thread.start();
        //休眠5s
        TimeUnit.SECONDS.sleep(5);
        System.out.println(thread.isDaemon());
        synchronized (lock) {
            lock.notify();
        }

        /**
         * 输出结果：
         * 1568202511535:t1 start
         * 1568202516535:t1 被唤醒
         *
         * t1线程中调用wait方法让t1线程等待，主线程中休眠5s之后，调用lock.notify()方法被唤醒了t1线程，输出的结果中，相差5s，程序正常退出
         *
         */
    }
}