package com.juc.chat20;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 需求：对访问量进行统计，用户每次发一次请求，计数器+1
 * 假设100个人同时访问，每个人发起10次请求，总访问数是1000才对
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/29
 */
public class Demo1 {

    /**
     * 访问次数
     */
    private static int count = 0;

    /**
     * 访问一次
     *
     * @throws InterruptedException
     */
    public static void request() throws InterruptedException {
        //模拟访问耗时操作
        TimeUnit.MILLISECONDS.sleep(5);
        count++;
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
         * main，耗时：124ms，count=997
         *
         * count用来记录访问的总次数，request()表示访问一次，内部休眠5ms模拟耗时操作，request()对count++操作。
         * 程序最终耗时1s多，count结果和预期不一致
         *
         */
    }

}