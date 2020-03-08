package com.juc.chat06;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/04
 */
public class Demo5 {

    /**
     * t2获取锁
     * t1获取锁
     * t3获取锁
     * t2获取锁
     * t1获取锁
     * t3获取锁
     * t1获取锁
     * t3获取锁
     * t2获取锁
     * t1获取锁
     * t3获取锁
     * t2获取锁
     * t1获取锁
     * t3获取锁
     * t2获取锁
     */
    private static ReentrantLock fairLock = new ReentrantLock(true);

    /**
     * t1获取锁
     * t3获取锁
     * t2获取锁
     * t1获取锁
     * t2获取锁
     * t3获取锁
     * t1获取锁
     * t2获取锁
     * t3获取锁
     * t3获取锁
     * t2获取锁
     * t1获取锁
     * t1获取锁
     * t2获取锁
     * t3获取锁
     */
//    private static ReentrantLock nonfairLock = new ReentrantLock();

    public static class T extends Thread {

        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    fairLock.lock();
                    System.out.println(this.getName() + "获取锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    fairLock.unlock();
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        T t1 = new T("t1");
        t1.start();
        T t2 = new T("t2");
        t2.start();
        T t3 = new T("t3");
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}