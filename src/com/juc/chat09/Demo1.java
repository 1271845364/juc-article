package com.juc.chat09;

import java.util.concurrent.TimeUnit;

/**
 * synchronized中等待和唤醒等待的线程
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/06
 */
public class Demo1 {

    static Object lock = new Object();

    public static class T1 extends Thread {
        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 准备获取锁");
            synchronized (lock) {
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 获取锁成功");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 释放锁成功");
        }
    }

    public static class T2 extends Thread {
        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 准备获取锁");
            synchronized (lock) {
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 获取锁成功");
                lock.notify();
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " notify");
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 准备释放锁");
            }
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 释放锁成功");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T1 t1 = new T1();
        t1.setName("t1");
        t1.start();

        TimeUnit.SECONDS.sleep(1);

        T2 t2 = new T2();
        t2.setName("t2");
        t2.start();

        /**
         * 1567771406894:t1 准备获取锁
         * 1567771406895:t1 获取锁成功
         * 1567771411897:t2 准备获取锁
         * 1567771411898:t2 获取锁成功
         * 1567771411898:t2 notify
         * 1567771416898:t2 准备释放锁
         * 1567771416899:t1 释放锁成功
         * 1567771416899:t2 释放锁成功
         *
         * 根据输出结果分析：
         * 1、线程1先获取锁，然后调用wait()方法线程1进入等待状态
         * 2、主线程等待5s，启动线程2，线程2获取到锁，结果1、3两行之间相差5s
         * 3、线程2调用lock.notify()方法，准备将等待在lock上的线程1唤醒，调用notify()方法的线程又休眠了5s，看结果5、8行之间相差5s，可以看出调用
         *    notify()方法之后，t1并不能立即被唤醒，需要等待线程2将synchronized()执行完毕，释放锁之后，线程1才会继续执行
         * 4、wait()和notify()方法必须放在同步块内调用(synchronized块内)，否则会报错
         */
    }

}