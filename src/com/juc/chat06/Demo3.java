package com.juc.chat06;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock实现共享变量的++操作
 * 与synchronized相比，ReentrantLock锁有明显的加锁和释放锁，手动何时加锁，何时释放锁，逻辑控制更灵活
 * unlock一定要放在finally中，否则如果程序出现异常，锁没有释放，其他线程就不能获取到这个锁了
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/04
 */
public class Demo3 {

    private static int num = 0;
    private static ReentrantLock lock = new ReentrantLock();

    public static class T extends Thread {
        @Override
        public void run() {
            Demo3.add();
        }
    }

    private static void add() {
        try {
            lock.lock();
            int ii = 1/0;
            for (int i = 0; i < 1000; i++) {
                num++;
            }
        } finally {
            System.out.println("unlock");
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