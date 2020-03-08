package com.juc.chat32;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 需求：一个jvm中实现一个计数器功能，需保证多线程情况下数据正确性。
 * 我们来模拟50个线程，每个线程对计数器递增100万次，最终结果应该是5000万。
 * <p>
 * 方式三：使用LongAdder实现
 *
 * LongAdder是jdk1.8出现的，提供的API可以替换掉AtomicLong。LongAdder在并发量比较大的情况下，操作数据的时候，相当于把这个数字分成了很多份数字，
 * 然后交给多个人去管控，每个管控者负责保证部分数字在多线程情况下操作的正确性。当多线程访问的时候，通过hash算法映射到具体管控者去操作数据，最后再
 * 汇总所有的管控者数据，得到最终结果。相当于降低了并发情况下的锁粒度，所以效率比较高
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/17
 */
public class Demo3 {

    static LongAdder count = new LongAdder();

    private static void incr() {
        count.increment();
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            count.reset();
            m1();
        }

        /**
         * 输出结果：
         * 结果：50000000，耗时(ms)：320
         * 结果：50000000，耗时(ms)：226
         * 结果：50000000，耗时(ms)：189
         * 结果：50000000，耗时(ms)：214
         * 结果：50000000，耗时(ms)：177
         * 结果：50000000，耗时(ms)：165
         * 结果：50000000，耗时(ms)：190
         * 结果：50000000，耗时(ms)：205
         * 结果：50000000，耗时(ms)：177
         * 结果：50000000，耗时(ms)：202
         *
         *
         * 平均耗时 200ms
         * new LongAdder创建一个对象，内部数字初始值是0，调用increment()方法可以对LongAdder内部的值原子递增1。reset方法可以重置LongAdder的值为0
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