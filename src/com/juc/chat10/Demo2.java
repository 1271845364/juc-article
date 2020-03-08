package com.juc.chat10;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/11
 */
public class Demo2 {

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
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
         * 1568202859992:t1 start
         * Exception in thread "t1" java.lang.IllegalMonitorStateException
         * 	at java.lang.Object.wait(Native Method)
         * 	at java.lang.Object.wait(Object.java:502)
         * 	at com.juc.chat10.Demo2$1.run(Demo2.java:19)
         * 	at java.lang.Thread.run(Thread.java:745)
         *
         * 删除了synchronized，发现调用wait()方法和调用notify()方法都抛出了IllegalMonitorStateException异常
         * 原因：Object类中的wait、notify、notifyAll用于线程等待和唤醒的方法，都必须在同步代码中进行(必须用到关键字synchronzied)
         *
         */
    }
}