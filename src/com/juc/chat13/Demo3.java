package com.juc.chat13;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 并行处理任务工具类，使用CountDownLatch实现
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo3 {


    public static class T extends Thread {
        //休眠的秒数
        int sleepSeconds;
        CountDownLatch countDownLatch;

        public T(String name, int sleepSeconds, CountDownLatch countDownLatch) {
            super(name);
            this.sleepSeconds = sleepSeconds;
            this.countDownLatch = countDownLatch;
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
            } finally {
                countDownLatch.countDown();
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime + "," + thread.getName() + ",处理完毕，耗时：" + (endTime - startTime) + "ms");
        }
    }

    /**
     * 2个线程解析2个sheet，主线程等待2个sheet解析完成。主线程说，我等待2秒，你们还是无法处理完成，就不等待了，直接返回
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "线程start");
        CountDownLatch countDownLatch = new CountDownLatch(2);

        long startTime = System.currentTimeMillis();
        T t1 = new T("解析sheet1线程", 2, countDownLatch);
        t1.start();

        T t2 = new T("解析sheet2线程", 5, countDownLatch);
        t2.start();

        boolean result = countDownLatch.await(2, TimeUnit.SECONDS);
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "线程end");

        long endTime = System.currentTimeMillis();
        System.out.println("主线程耗时：" + (endTime - startTime) + " ms,result:" + result);

        /**
         * 输出结果：
         * 1568719723449,main线程start
         * 1568719723465,解析sheet1线程,开始处理！
         * 1568719723465,解析sheet2线程,开始处理！
         * 1568719725480,main线程end
         * 1568719725480,解析sheet1线程,处理完毕，耗时：2015ms
         * 主线程耗时：2031 ms,result:false
         * 1568719728480,解析sheet2线程,处理完毕，耗时：5015ms
         *
         * 线程2耗时了5s，主线程耗时2s，主线程中调用countDownLatch.await(2, TimeUnit.SECONDS);
         * 表示最多等待2s，不管计数器是否为0，await()方法都会返回，若等待时间内，计数器变为了0，立即返回true，否则超时返回false
         *
         */
    }
}