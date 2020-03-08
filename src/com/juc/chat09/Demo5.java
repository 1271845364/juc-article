package com.juc.chat09;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition.await(long time,TimeUnit unit)超时之后自动返回
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/10
 */
public class Demo5 {

    static Lock lock = new ReentrantLock();

    static Condition condition = lock.newCondition();

    private static class T1 extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + ":start");
                boolean await = condition.await(2, TimeUnit.SECONDS);
                System.out.println(await);
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
        /**
         * t1线程等待2s之后，自动返回false，继续执行，await()返回false表示超时之后自动返回
         */
    }

}