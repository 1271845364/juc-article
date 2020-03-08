package com.juc.chat06;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock可重入锁
 * 1、lock()和unlock()必须成对出现，防止后面的线程不能获取锁
 * 2、unlock()必须放在finally中，保证程序无论是否有异常，锁一定会被释放
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/04
 */
public class Demo4 {

    private static int num = 0;
    private static ReentrantLock lock = new ReentrantLock();

    public static class T extends Thread {
        @Override
        public void run() {
            Demo4.add();
        }
    }

    /**
     * 一个线程进入add()，执行两次获取锁操作，程序可以正常结束，输出结果也是正确的；假设ReentrantLock不是可重入锁，同一个线程在
     * 第二次获取锁的时候由于前面获取锁还未释放而导致死锁，程序是无法正常结束的
     */
    private static void add() {
        try {
            lock.lock();
            lock.lock();
            for (int i = 0; i < 1000; i++) {
                num++;
            }
        } finally {
            lock.unlock();
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        T t1 = new T();
        t1.start();
        T t2 = new T();
        t2.start();
        T t3 = new T();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(num);
    }
}