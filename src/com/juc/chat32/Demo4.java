package com.juc.chat32;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * 需求：一个jvm中实现一个计数器功能，需保证多线程情况下数据正确性。
 * 我们来模拟50个线程，每个线程对计数器递增100万次，最终结果应该是5000万。
 * <p>
 * 方式四：使用LongAccumulator实现
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/17
 */
public class Demo4 {

    static LongAccumulator count = new LongAccumulator((x,y)->x+y,0);

    private static void incr() {
        count.accumulate(1);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            count.reset();
            m1();
        }

        /**
         * 输出结果：
         * 结果：50000000，耗时(ms)：212
         * 结果：50000000，耗时(ms)：211
         * 结果：50000000，耗时(ms)：221
         * 结果：50000000，耗时(ms)：186
         * 结果：50000000，耗时(ms)：171
         * 结果：50000000，耗时(ms)：200
         * 结果：50000000，耗时(ms)：184
         * 结果：50000000，耗时(ms)：175
         * 结果：50000000，耗时(ms)：245
         * 结果：50000000，耗时(ms)：221
         *
         *
         * 平均耗时 200ms
         *
         * LongAccumulate和LongAdder效率差不多，不过更灵活一些
         * 调用new LongAdder()和new LongAccumulator((x,y)->x+y,0)一样
         *
         *
         * 从上面4个示例的结果来看，LongAdder、LongAccumulator全面超越同步锁及AtomicLong的方式，
         * 建议在使用AtomicLong的地方可以直接替换为LongAdder、LongAccumulator，吞吐量更高一些。
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