package com.juc.chat32;

import java.util.concurrent.CountDownLatch;

/**
 * 需求：一个jvm中实现一个计数器功能，需保证多线程情况下数据正确性。
 * 我们来模拟50个线程，每个线程对计数器递增100万次，最终结果应该是5000万。
 * <p>
 * 方式一：使用synchronized实现
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/17
 */
public class Demo1 {

    static int count = 0;

    private static synchronized void incr() {
        count++;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            count = 0;
            m1();
        }

        /**
         * 输出结果：
         * 结果：50000000，耗时(ms)：1959
         * 结果：50000000，耗时(ms)：659
         * 结果：50000000，耗时(ms)：689
         * 结果：50000000，耗时(ms)：717
         * 结果：50000000，耗时(ms)：684
         * 结果：50000000，耗时(ms)：683
         * 结果：50000000，耗时(ms)：683
         * 结果：50000000，耗时(ms)：713
         * 结果：50000000，耗时(ms)：730
         * 结果：50000000，耗时(ms)：652
         *
         * 平均耗时 700ms
         *
         */
    }

    private static void m1() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        int threadCount = 50;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 1000000; j++) {
                        incr();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        countDownLatch.await();
        long t2 = System.currentTimeMillis();
        System.out.println(String.format("结果：%s，耗时(ms)：%s", count, (t2 - t1)));
    }

}