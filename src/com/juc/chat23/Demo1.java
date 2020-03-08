package com.juc.chat23;

import sun.misc.Unsafe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 需求：使用AtomicInteger实现网站访问量计数器功能，模拟100人同时访问网站，每个人访问10次
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/07
 */
public class Demo1 {

    /**
     * 访问次数
     */
    private static AtomicInteger count = new AtomicInteger();

    /**
     * 请求一次
     */
    public static void request() throws InterruptedException {
        //模拟耗时5ms
        TimeUnit.MILLISECONDS.sleep(5);
        //对count原子操作+1
        count.incrementAndGet();
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int threadSize = 100;
        CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        for (int i = 0; i < threadSize; i++) {
            Thread thread = new Thread(()->{
                try {
                    for(int j=0;j<10;j++) {
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
         * main，耗时：141ms，count=1000
         *
         * incrementAndGet()在多线程情况下能确保数据的正确
         *
         */
    }
}