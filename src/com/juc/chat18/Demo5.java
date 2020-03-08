package com.juc.chat18;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可能定时任务执行一会，想取消执行，可以调用scheduledFuture.cancel(boolean mayInterruptIfRunning); 参数为是否发送中断信号
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/26
 */
public class Demo5 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.currentTimeMillis());
        //任务执行次数计数器
        AtomicInteger atomicInteger = new AtomicInteger(1);

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            int currentCount = atomicInteger.getAndIncrement();
            System.out.println(Thread.currentThread().getName());
            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次开始执行");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次执行结束");
        }, 1, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);
        scheduledFuture.cancel(true);
        TimeUnit.SECONDS.sleep(1);
        System.out.println("任务是否被取消：" + scheduledFuture.isCancelled());
        System.out.println("任务是否已完成：" + scheduledFuture.isDone());

        /**
         * 输出结果：
         * 1569496645998
         * pool-1-thread-1
         * 1569496647091第1次开始执行
         * 1569496649092第1次执行结束
         * pool-1-thread-1
         * 1569496650093第2次开始执行
         * java.lang.InterruptedException: sleep interrupted
         * 	at java.lang.Thread.sleep(Native Method)
         * 	at java.lang.Thread.sleep(Thread.java:340)
         * 	at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
         * 	at com.juc.chat18.Demo5.lambda$main$0(Demo5.java:25)
         * 	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
         * 	at java.util.concurrent.FutureTask.runAndReset$$$capture(FutureTask.java:308)
         * 	at java.util.concurrent.FutureTask.runAndReset(FutureTask.java)
         * 	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180)
         * 	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294)
         * 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
         * 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
         * 	at java.lang.Thread.run(Thread.java:745)
         * 1569496651094第2次执行结束
         * 任务是否被取消：true
         * 任务是否已完成：true
         *
         *
         * 可以看到任务被取消成功了
         *
         *
         */
    }

}