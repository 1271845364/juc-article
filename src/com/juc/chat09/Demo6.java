package com.juc.chat09;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition.await(long time,TimeUnit unit)超时之前被唤醒
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/10
 */
public class Demo6 {

    static Lock lock = new ReentrantLock();

    static Condition condition = lock.newCondition();

    private static class T1 extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + ":start");
                boolean await = condition.await(5, TimeUnit.SECONDS);
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

        //休眠1s，唤醒t1
        TimeUnit.SECONDS.sleep(1);
        lock.lock();
        try {
            condition.signal();
        }finally {
            lock.unlock();
        }

        /**
         * 线程t1中调用condition.await(5，TimeUnit.SECONDS)方法会释放锁，等待5s，主线程休眠1s，然后获取锁，之后调用
         * condition.signal()方法唤醒等待的线程(t1)，输出结果中发现await()后过了1s，await()就返回了，并且返回值为true。
         * true表示await()方法超时之前被其他线程唤醒了
         */
    }

}