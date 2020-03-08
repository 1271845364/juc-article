package com.juc.chat23;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 需求：统计网站页面访问量，假设网站有10个页面，现在模拟100个人并行访问每个页面10次，然后将每个页面访问量输出，应该每个页面都是1000次
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/07
 */
public class Demo2 {

    /**
     * 每个页面访问次数
     */
    private static AtomicIntegerArray pageRequest = new AtomicIntegerArray(new int[10]);

    /**
     * 请求一次
     *
     * @param page 访问的第几个页面
     */
    public static void request(int page) throws InterruptedException {
        //模拟耗时5ms
        TimeUnit.MILLISECONDS.sleep(5);
        //pageCountIndex为pageCount数组的下标，表示页面的对应数组中的位置
        int pageCountIndex = page - 1;
        pageRequest.incrementAndGet(pageCountIndex);
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int threadSize = 100;
        CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        for (int i = 0; i < threadSize; i++) {
            Thread thread = new Thread(() -> {
                try {
                    //10个页面
                    for (int page = 1; page <= 10; page++) {
                        //每个页面访问10次
                        for (int j = 0; j < 10; j++) {
                            request(page);
                        }
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
        System.out.println(Thread.currentThread().getName() + "，耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        for (int pageIndex = 0; pageIndex < 10; pageIndex++) {
            System.out.println("第" + (pageIndex + 1) + "个页面访问次数为" + pageRequest.get(pageIndex));
        }

        /**
         * 输出结果：
         * main，耗时：706ms
         * 第1个页面访问次数为1000
         * 第2个页面访问次数为1000
         * 第3个页面访问次数为1000
         * 第4个页面访问次数为1000
         * 第5个页面访问次数为1000
         * 第6个页面访问次数为1000
         * 第7个页面访问次数为1000
         * 第8个页面访问次数为1000
         * 第9个页面访问次数为1000
         * 第10个页面访问次数为1000
         *
         * 代码中将10个页面的访问量放在了一个int类型的数组中，数组大小为10，然后通过AtomicIntegerArray来操作数组中的每个元素，
         * 可以确保操作数据的原子性，每次访问会调用incrementAndGet，此方法需要传入数组的下标，然后对指定的元素做原子+1操作。输出结果
         * 都是1000，可以看出对于数组中元素的并发修改是线程安全的。如果线程不安全，则部分数据可能会<1000
         *
         */
    }
}