package com.juc.chat13;

import java.util.concurrent.TimeUnit;

/**
 * 并行处理任务工具类，使用线程的join()方法
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo1 {

    public static class T extends Thread {
        //休眠的秒数
        int sleepSeconds;

        public T(String name, int sleepSeconds) {
            super(name);
            this.sleepSeconds = sleepSeconds;
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            long startTime = System.currentTimeMillis();
            System.out.println(startTime + "," + thread.getName() + ",开始处理！");
            try {
                //模拟耗时操作
                TimeUnit.SECONDS.sleep(sleepSeconds);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime + "," + thread.getName() + ",处理完毕，耗时：" + (endTime - startTime) + "ms");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        T t1 = new T("解析sheet1线程", 2);
        t1.start();

        T t2 = new T("解析sheet2线程", 5);
        t2.start();

        t1.join();
        t2.join();
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime) + " ms");

        /**
         * 输出结果：
         * 1568717362378,解析sheet1线程,开始处理！
         * 1568717362378,解析sheet2线程,开始处理！
         * 1568717364393,解析sheet1线程,处理完毕，耗时：2015ms
         * 1568717367409,解析sheet2线程,处理完毕，耗时：5031ms
         * 总耗时：5031 ms
         *
         * 启动了两个解析sheet的线程，第一个耗时2s，第二个耗时5s，最终结果中耗时5s。
         * 线程的join()方法，此方法会让当前线程等待被调用的线程完成之后才能继续。
         * 看下join()源码，内部其实是在synchronized中调用wait()方法，最后被调用
         * 的线程执行完毕之后，由jvm自动调用其notifyAll()方法，唤醒所有等待中的线程。
         * 这个notifyAll()方法是由jvm内部自动调用的，jdk源码中是看不到的，需要看jvm源码
         * 所以JDK不推荐在线程上调用wait、notify、notifyAll方法。
         *
         */
    }
}