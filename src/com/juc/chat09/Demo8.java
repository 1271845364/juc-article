package com.juc.chat09;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * condition.awaitNanos(long nanosTimeout)超时之前被其他线程唤醒
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/10
 */
public class Demo8 {

    static Lock lock = new ReentrantLock();

    static Condition condition = lock.newCondition();

    private static class T1 extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + ":start");
                long l = condition.awaitNanos(TimeUnit.SECONDS.toNanos(5));
                System.out.println(l);
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + ":end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T1 t1 = new T1();
        t1.setName("t1");
        t1.start();

        TimeUnit.SECONDS.sleep(1);
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }

        /**
         * 输出结果：
         * 1568119467684:t1:start
         * 4001764400
         * 1568119468684:t1:end
         *
         * 线程t1中调用await()休眠5s，主线程休眠1s之后，调用signal()唤醒线程t1，await()方法返回整数，
         * 表示返回时距离超时时间还有多久，将近4s，返回正数表示，线程在超时之前被唤醒了
         *
         *
         * 其他的几个有参的await方法和无参的await方法一样，线程调用interrupt()方法时，这些方法都会触发InterruptedException异常，
         * 并且线程的中断标志会被清除
         *
         */
    }

}