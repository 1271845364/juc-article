package com.juc.chat18;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用ScheduleThreadPoolExecutor的scheduleWithFixedDelay方法，该方法设置了执行周期，
 * 与scheduleAtFixedRate方法不同的是，下一次执行时间是上一次任务执行完的系统时间加上period，
 * 因而具体执行时间不是固定的，但周期是固定的，是采用相对固定的延迟来执行任务。
 *
 * public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
 *                                                      long initialDelay,
 *                                                      long delay,
 *                                                      TimeUnit unit);
 * 参数：
 *  command：待执行的任务
 *  initialDelay：延迟多久执行第一次
 *  delay：表示下次执行时间和上次执行结束时间之间的间隔时间
 *  unit：时间单位
 *
 *
 * 假设系统调用scheduleAtFixedRate的时间是T1，那么执行时间如下：
 *
 * 第1次：T1+initialDelay，执行结束时间：E1
 *
 * 第2次：E1+delay，执行结束时间：E2
 *
 * 第3次：E2+delay，执行结束时间：E3
 *
 * 第4次：E3+delay，执行结束时间：E4
 *
 * 第n次：上次执行结束时间+delay
 *
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/26
 */
public class Demo3 {

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        //任务执行次数计数器
        AtomicInteger atomicInteger = new AtomicInteger(1);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10, Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            int currentCount = atomicInteger.getAndIncrement();
            System.out.println(Thread.currentThread().getName());
            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次开始执行");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次执行结束");
        }, 1, 3, TimeUnit.SECONDS);

        /**
         * 输出结果：
         * 1569495330601
         * pool-1-thread-1
         * 1569495331656第1次开始执行
         * 1569495333658第1次执行结束
         * pool-1-thread-1
         * 1569495336659第2次开始执行
         * 1569495338660第2次执行结束
         * pool-1-thread-2
         * 1569495341661第3次开始执行
         * 1569495343661第3次执行结束
         * pool-1-thread-1
         * 1569495346666第4次开始执行
         * 1569495348667第4次执行结束
         *
         * 延迟1s后执行第1次，后面每次的执行时间和上次执行结束时间间隔3s
         *
         */

    }

}