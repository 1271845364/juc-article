package com.juc.chat13;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 并行处理任务工具类，使用CountDownLatch实现
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo2 {


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

    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "线程start");
        CountDownLatch countDownLatch = new CountDownLatch(2);
        T t1 = new T("解析sheet1线程", 2,countDownLatch);
        t1.start();

        T t2 = new T("解析sheet2线程", 5,countDownLatch);
        t2.start();

        countDownLatch.await();
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "线程end");

        /**
         * 输出结果：
         * 1568718901509,main线程start
         * 1568718901509,解析sheet1线程,开始处理！
         * 1568718901509,解析sheet2线程,开始处理！
         * 1568718903525,解析sheet1线程,处理完毕，耗时：2016ms
         * 1568718906525,解析sheet2线程,处理完毕，耗时：5016ms
         * 1568718906525,main线程end
         *
         * 从输出结果中看到，和join()效果是一样的，创建CountDownLatch计数器为2，主线程中调用countDownLatch.await()；
         * 会让主线程等待，t1、t2线程中模拟执行耗时操作，最终在finally中调用countDownLatch.countDown();该方法调用一次，
         * CountDownLatch内部计数器就会减1，当计数器变为0的时候，主线程中的await()会返回，然后继续执行。
         * 注意：
         * CountDownLatch.countDown()这个方法必须执行，所以放在finally中
         *
         */
    }
}