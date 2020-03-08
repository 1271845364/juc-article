package com.juc.chat18;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/26
 */
public class Demo4 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.currentTimeMillis());
        //任务执行次数计数器
        AtomicInteger atomicInteger = new AtomicInteger(1);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            int currentCount = atomicInteger.getAndIncrement();
            System.out.println(Thread.currentThread().getName());
            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次开始执行");
            System.out.println(10 / 0);
            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次执行结束");
        }, 1, 3, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);
        //任务是否被取消
        System.out.println(scheduledFuture.isCancelled());
        //任务是否执行完成
        System.out.println(scheduledFuture.isDone());


        /**
         * 输出结果：
         * 1569495751410
         * pool-1-thread-1
         * 1569495752462第1次开始执行
         * false
         * true
         *
         * 输出上面的内容，程序也没有结束
         *
         *
         * 10/0那触发异常，发生异常之后就没有任何现象了，被ScheduledExecutorService内部给吞掉了，然后这个任务再也不执行了
         * scheduledFuture.isDone()输出true，表示这个任务已经结束了，再也不会被执行了
         *
         * 在scheduleWithFixedDelay(Runnable command,long initialDelay,long delay,TimeUnit unit)
         * 该command中的run方法中一定要try-catch
         *
         */
    }

}