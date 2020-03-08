package com.juc.chat16;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/19
 */
public class Demo1 {

    private static class ThreadPoolName implements ThreadFactory {

        private final String prefix;

        private final AtomicInteger atomicInteger = new AtomicInteger(1);

        public ThreadPoolName(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            String threadPoolPrefix = prefix + "-thread-";
            ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
            Thread thread = new Thread(threadGroup, r, threadPoolPrefix + atomicInteger.getAndIncrement(), 0);
            return thread;
        }
    }

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 5, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadPoolName("my"), new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int j = i;
            String taskName = "任务" + j;
            executor.execute(() -> {
                //模拟任务内部处理耗时
                try {
                    TimeUnit.SECONDS.sleep(j);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + taskName + "处理完毕");
            });
        }
        //关闭线程池
        executor.shutdown();

        /**
         * 输出结果：
         * my-thread-1任务0处理完毕
         * my-thread-2任务1处理完毕
         * my-thread-3任务2处理完毕
         * my-thread-1任务3处理完毕
         * my-thread-2任务4处理完毕
         * my-thread-3任务5处理完毕
         * my-thread-1任务6处理完毕
         * my-thread-2任务7处理完毕
         * my-thread-3任务8处理完毕
         * my-thread-1任务9处理完毕
         *
         *
         * 线程池中5种常见的工作队列
         * 任务太多的时候，工作队列用户暂时缓存待处理的任务，jdk中5中常见的队列
         * 1、ArrayBlockQueue：是一个基于数组结构的有界阻塞队列，按照先进先出原则对元素进行排序
         * 2、LinkedBlockQueue：是一个基于链表结构的阻塞队列，按照先进先出排序，吞吐量要高于ArrayBlockQueue。静态工厂方法使用了Executors.newFixedThreadPool()使用了这个队列
         * 3、SynchronousQueue：一个不存储元素的阻塞队列，每个插入操作必须等到另外一个线程调用移除操作，否则插入操作一直处于阻塞状态，吞吐量通常要高于
         *    LinkedBlockQueue,静态工厂方法Executors.newCachedThreadPool()使用这种队列
         * 4、PriorityBlockingQueue：优先级队列，进入队列的元素按照优先级会进行排序
         *
         *
         */

    }

}