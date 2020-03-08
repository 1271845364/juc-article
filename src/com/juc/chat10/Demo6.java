package com.juc.chat10;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 唤醒代码在等待之前执行
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/11
 */
public class Demo6 {

    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                try {
                    System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
                } finally {
                    lock.unlock();
                }
            }
        });

        thread.setName("t1");
        thread.start();
        //休眠1s
        TimeUnit.SECONDS.sleep(1);
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
        System.out.println(System.currentTimeMillis() + ",condition.signal();执行完毕");

        /**
         * 输出结果：
         * 1568207705551,condition.signal();执行完毕
         * 1568207709552:t1 start
         *
         * 程序无法结束，代码结合输出可以看出signal()方法在await()方法之前执行的，
         * 最终t1线程无法被唤醒，导致程序无法结束
         *
         *
         * Condition中方法总结：
         * 1、使用Condition中的线程等待和唤醒方法之前，需要先获取锁。否则会报IllegalMonitorStateException异常
         * 2、signal()方法先于await()方法之前调用，线程无法被唤醒
         *
         *
         * Object和Condition的局限：
         * 1、线程等待和唤醒的方法能够执行的先决条件是：线程需要先获取锁
         * 2、唤醒方法需要在等待方法之后调用，线程才能够被唤醒
         *
         */
    }
}