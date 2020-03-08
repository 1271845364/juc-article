package com.juc.chat10;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/12
 */
public class Demo7 {

    /**
     * 主线程等待5s，唤醒t1线程
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
            LockSupport.park();
            System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
        });
        t1.setName("t1");
        t1.start();

        //休眠5s
        TimeUnit.SECONDS.sleep(5);
        LockSupport.unpark(t1);
        System.out.println(System.currentTimeMillis() + "Lock.unpark()执行完毕");

        /**
         * 输出：
         * 1568283294079:t1 start
         * 1568283299080Lock.unpark()执行完毕
         * 1568283299080:t1 被唤醒
         *
         * t1线程中调用LockSupport.park();让当前线程t1等待，主线程休眠5s之后，调用LockSupport.unpark(t1)，将t1
         * 线程唤醒，输出结果中正好相差5s，说明t1线程等待5s之后，被唤醒了
         *
         * LockSupport.park();无参数，内部直接会让当前线程处于等待中；unpark方法传递了一个线程作为参数，表示将对应的线程唤醒
         *
         */
    }
}