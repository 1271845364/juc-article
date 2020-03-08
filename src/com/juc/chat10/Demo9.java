package com.juc.chat10;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * park()让线程等待之后，是否能够响应线程中断
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/16
 */
public class Demo9 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
            System.out.println(Thread.currentThread().getName() + ",park()之前中断标志：" + Thread.currentThread().isInterrupted());
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + ",park()之后中断标志：" + Thread.currentThread().isInterrupted());
            System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
        });
        t1.setName("t1");
        t1.start();

        //休眠5s
        TimeUnit.SECONDS.sleep(5);
        t1.interrupt();

        /**
         * 输出：
         * 1568631164704:t1 start
         * t1,park()之前中断标志：false
         * t1,park()之后中断标志：true
         * 1568631169704:t1 被唤醒
         *
         * t1线程中调用了park()方法让线程等待，主线程休眠了5s之后，调用t1.interrupt()；给线程t1发送中断信号，然后线程t1从等待中被唤醒了，
         * 输出的结果中，可以看出相差5s左右，刚好是主线程休眠了5s之后将t1唤醒了。
         *
         * 结论：park方法可以响应线程中断
         *
         * LockSupport.park方法让线程等待之后，唤醒方式有2种：
         * 1、调用LockSupport.unpark方法
         * 2、调用等待线程的interrupt()方法，给等待的线程发送中断信号，可以唤醒线程
         *
         */
    }
}