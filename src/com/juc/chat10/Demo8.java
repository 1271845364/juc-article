package com.juc.chat10;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 唤醒方法放在等待方法之前
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/12
 */
public class Demo8 {

    /**
     * 主线程等待5s，唤醒t1线程
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
            LockSupport.park();
            System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
        });
        t1.setName("t1");
        t1.start();

        //休眠1s
        TimeUnit.SECONDS.sleep(1);
        LockSupport.unpark(t1);
        System.out.println(System.currentTimeMillis() + "Lock.unpark()执行完毕");

        /**
         * 输出：
         * 1568284182091Lock.unpark()执行完毕
         * 1568284186104:t1 start
         * 1568284186104:t1 被唤醒
         *
         * 启动线程t1，t1线程内部休眠5s后，然后主线程休眠1s后，调用LockSupport.unpark(t1)；唤醒线程t1，
         * 此时LockSupport.park()方法还未执行，说明唤醒方法在等待方法之前执行；输出结果中第2和3行时间是一样的
         * 表示LockSupport.park()没有阻塞了，是立即返回的
         *
         * 说明：
         * 唤醒方法在等待方法之前执行，线程也能够被唤醒，这点是另外2种方法无法做到的。Object和Condition中的唤醒必须在等待之后调用，线程才能被唤醒。
         * 而LockSupport中，唤醒的方法不管是在等待之前还是在等待之后调用，线程都能够被唤醒。
         */
    }
}