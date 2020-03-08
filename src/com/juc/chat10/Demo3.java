package com.juc.chat10;

import java.util.concurrent.TimeUnit;

/**
 * 唤醒方法在等待之前执行，不能被唤醒
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/11
 */
public class Demo3 {

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        //休眠1s之后唤醒lock对象上等待的线程
        TimeUnit.SECONDS.sleep(1);
        System.out.println(thread.isDaemon());
        synchronized (lock) {
            lock.notify();
        }
        System.out.println("lock notify()执行完毕");

        /**
         * 输出结果：
         * false
         * lock notify()执行完毕
         * 1568203576924:t1 start
         *
         * 程序无法结束，t1线程调用wait()方法之后无法被唤醒了，从输出中可以看出，notify方法在wait方法之前执行了
         * 等待的线程无法被唤醒了。说明：唤醒方法在等待方法之前执行，线程无法被唤醒
         *
         *
         * Object类中的用户线程等待/唤醒的方法总结：
         * 1、wait/notify/notifyAll方法都必须放在同步代码块(必须在synchronized)中执行，需要先获取锁
         * 2、线程唤醒(notify/notifyAll)需要在等待的方法(wait)之后执行，等待中的线程才能会被唤醒，否则无法被唤醒
         */
    }
}