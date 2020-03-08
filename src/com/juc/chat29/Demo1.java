package com.juc.chat29;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 需求：
 * 以秒杀业务为例，10个iphone，100万人抢购，100万人同时发起请求，最终能够抢到的人也就是前面几个人，
 * 后面的基本上都没有希望了，那么我们可以通过控制并发数来实现，比如并发数控制在10个，其他超过并发数的请求全部拒绝，提示：秒杀失败，请稍后重试。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/15
 */
public class Demo1 {

    private static Semaphore semaphore = new Semaphore(5);

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                boolean flag = false;
                try {
                    flag = semaphore.tryAcquire(100, TimeUnit.MICROSECONDS);
                    if (flag) {
                        System.out.println(Thread.currentThread().getName() + "，尝试下单中...");
                        //模拟下单
                        TimeUnit.SECONDS.sleep(2);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "，秒杀失败，请稍后重试");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (flag) {
                        semaphore.release();
                    }
                }
            }).start();
        }

        /**
         * 输出结果：
         * Thread-1，尝试下单中...
         * Thread-0，尝试下单中...
         * Thread-2，尝试下单中...
         * Thread-3，尝试下单中...
         * Thread-4，尝试下单中...
         * Thread-5，秒杀失败，请稍后重试
         * Thread-6，秒杀失败，请稍后重试
         * Thread-9，秒杀失败，请稍后重试
         * Thread-10，秒杀失败，请稍后重试
         * Thread-11，秒杀失败，请稍后重试
         * Thread-12，秒杀失败，请稍后重试
         * Thread-13，秒杀失败，请稍后重试
         * Thread-14，秒杀失败，请稍后重试
         * Thread-17，秒杀失败，请稍后重试
         * Thread-18，秒杀失败，请稍后重试
         * Thread-19，秒杀失败，请稍后重试
         * Thread-7，秒杀失败，请稍后重试
         * Thread-8，秒杀失败，请稍后重试
         * Thread-16，秒杀失败，请稍后重试
         * Thread-15，秒杀失败，请稍后重试
         *
         */
    }
}