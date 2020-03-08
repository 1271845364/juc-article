package com.juc.chat09;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * condition.awaitNanos(long nanosTimeout)超时返回
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/10
 */
public class Demo7 {

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

        /**
         * 返回结果：
         * 1568115810495:t1:start
         * -2100900
         * 1568115815499:t1:end
         *
         *
         * awaitNanos方法参数为纳秒，可以用TimeUnit中的一些方法将时间转换为纳秒
         * 线程t1调用awaitNanos方法等待5s超时返回，返回结果为负数，表示超时之后返回的
         *
         */

    }

}