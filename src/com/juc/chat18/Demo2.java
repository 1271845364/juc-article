package com.juc.chat18;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * scheduleAtFixedRate:固定的频率执行任务，该方法设置了执行周期，
 * 下一次执行时间相当于是上一次的执行时间加上period，任务每次执行完毕之后才会计算下次的执行时间。
 *
 * public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
 *                                                   long initialDelay,
 *                                                   long period,
 *                                                   TimeUnit unit);
 * 参数说明：
 *  command：待执行的任务
 *  initialDelay：延迟多久执行第一次
 *  period：连续执行之间的间隔
 *  unit：时间单位
 *
 *
 * 假设系统调用scheduleAtFixedRate的时间是T1，那么执行时间如下：
 *
 * 第1次：T1+initialDelay
 *
 * 第2次：T1+initialDelay+period
 *
 * 第3次：T1+initialDelay+2*period
 *
 * 第n次：T1+initialDelay+(n-1)*period
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/26
 */
public class Demo2 {

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        //任务执行次数计数器
        AtomicInteger atomicInteger = new AtomicInteger(1);
//        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10,Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            int currentCount = atomicInteger.getAndIncrement();
//            System.out.println(Thread.currentThread().getName());
//            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次开始执行");
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(System.currentTimeMillis() + "第" + currentCount + "次执行结束");
//        }, 1, 1, TimeUnit.SECONDS);


        /**
         * 输出结果：
         * 1569486736945
         * pool-1-thread-1
         * 1569486738003第1次开始执行
         * 1569486740004第1次执行结束
         * pool-1-thread-1
         * 1569486740004第2次开始执行
         * 1569486742005第2次执行结束
         * pool-1-thread-2
         * 1569486742005第3次开始执行
         * 1569486744006第3次执行结束
         * pool-1-thread-1
         * 1569486744006第4次开始执行
         * 1569486746006第4次执行结束
         * pool-1-thread-3
         * 1569486746006第5次开始执行
         * 1569486748007第5次执行结束
         * pool-1-thread-2
         * 1569486748007第6次开始执行
         * 1569486750007第6次执行结束
         * pool-1-thread-4
         * 1569486750007第7次开始执行
         * 1569486752007第7次执行结束
         * pool-1-thread-1
         * 1569486752007第8次开始执行
         * 1569486754007第8次执行结束
         * pool-1-thread-5
         * 1569486754008第9次开始执行
         * 1569486756009第9次执行结束
         * pool-1-thread-3
         * 1569486756009第10次开始执行
         *
         *
         *
         *
         *
         */


        /**
         * scheduleAtFixedRate()中执行的任务如果在period时间内没处理完，就执行完之后继续重复执行，如果在period时间内处理完了，
         * 就等到计算period那个时刻在触发执行
         */
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2,Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        scheduledExecutorService.scheduleAtFixedRate(()->{
            List<Integer> list = Arrays.asList(0,1,2,3,4,5,6,7,8,9);
            System.out.println(System.currentTimeMillis());
            System.out.println("list=" + list);
            for (Integer i : list) {
                scheduledExecutorService.schedule(new T(i),2,TimeUnit.SECONDS);
            }
        },1,3, TimeUnit.SECONDS);

    }

    static class T implements Runnable {
        private Integer num;

        public T(Integer i) {
            this.num = i;
        }

        @Override
        public void run() {
            System.out.println(this.num);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis() + " runnable");
        }
    }
}