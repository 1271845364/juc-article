package com.juc.chat32;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 需求：一个jvm中实现一个计数器功能，需保证多线程情况下数据正确性。
 * 我们来模拟50个线程，每个线程对计数器递增100万次，最终结果应该是5000万。
 * <p>
 * 方式二：使用AtomicLong实现
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/17
 */
public class Demo2 {

    static AtomicLong count = new AtomicLong(0);

    private static void incr() {
        count.incrementAndGet();
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            count.set(0);
            m1();
        }

        /**
         * 输出结果：
         * 结果：50000000，耗时(ms)：1294
         * 结果：50000000，耗时(ms)：1274
         * 结果：50000000，耗时(ms)：1272
         * 结果：50000000，耗时(ms)：1307
         * 结果：50000000，耗时(ms)：1270
         * 结果：50000000，耗时(ms)：1182
         * 结果：50000000，耗时(ms)：1285
         * 结果：50000000，耗时(ms)：1287
         * 结果：50000000，耗时(ms)：1307
         * 结果：50000000，耗时(ms)：1303
         *
         * 平均耗时 1250ms
         * AtomicLong内部采用的是CAS的方式实现的，并发量大的情况下，CAS失败率比较高，导致性能比synchronized还低一些。并发量不是特别大的情况下，
         * CAS性能还是可以的
         * AtomicLong属于JUC的原子类
         *
         */
    }

    private static void m1() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        Thread thread = new Thread();
        thread.join();
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