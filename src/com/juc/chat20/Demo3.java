package com.juc.chat20;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 需求：对访问量进行统计，用户每次发一次请求，计数器+1
 * 假设100个人同时访问，每个人发起10次请求，总访问数是1000才对
 *
 * 我们是否可以只在第3步加锁，减少加锁的范围，对第3步做以下处理：
 *
 * 获取锁
 * 第三步获取一下count最新的值，记做LV
 * 判断LV是否等于A，如果相等，则将B的值赋给count，并返回true，否者返回false
 * 释放锁
 *
 * 如果第3步返回的是false，我们就再次去获取count，将count值赋值给A，对A+1赋值给B，然后在将A、B的值带入到上面的过程中执行，直到上面的结果为true为止
 *
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/29
 */
public class Demo3 {

    /**
     * 访问次数
     */
    private static volatile int count = 0;

    /**
     * 访问一次
     *
     * @throws InterruptedException
     */
    public static void request() throws InterruptedException {
        //模拟访问耗时操作
        TimeUnit.MILLISECONDS.sleep(5);
        int expectCount;
        do{
            expectCount = count;
        }while(!compareAndSwap(expectCount,expectCount+1));
    }

    private static synchronized boolean compareAndSwap(int expectCount, int newCount) {
        //如果不相等说明这个count值被别的线程修改了
        if(count == expectCount) {
            count = newCount;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int threadSize = 100;
        CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        for (int i = 0; i < threadSize; i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        request();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
            thread.start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "，耗时：" + (System.currentTimeMillis() - startTime) + "ms，count=" + count);

        /**
         * 输出结果：
         * main，耗时：135ms，count=1000
         *
         * volatile修饰count，表示在多线程情况下访问count是可见性
         *
         */
    }

}