package com.juc.chat16;

import java.util.concurrent.*;

/**
 * 四种常见饱和策略
 * 当线程池中队列已满，并且线程池已达到最大线程数，线程池会将任务传递给饱和策略进行处理。
 * 这些策略都实现了RejectedExecutionHandler 接口
 * void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
 * 参数说明：
 * r：需要执行的任务
 * executor：当前线程池对象
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/24
 */
public class Demo5 {

    /**
     * JDK中提供了四种常见的饱和策略
     * 1、AbortPolicy 直接抛出异常
     * 2、CallerRunsPolicy 在当前调用者的线程中运行任务，即谁丢来的任务，由他自己去处理
     * 3、DiscardOldestPolicy 丢弃队列中最老的一个任务，即丢弃队列头部的任务，然后执行当前传入的任务
     * 4、DiscardPolicy 不处理，直接抛弃掉，方法内部为空
     */

    static class Task implements Runnable {

        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "处理" + this.name);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + name + '\'' +
                    '}';
        }

    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), Executors.defaultThreadFactory(), (r, executors) -> {
            //自定义饱和策略
            //记录一下无法处理的任务
            System.out.println("无法处理的任务：" + r.toString());
        });

        for (int i = 0; i < 5; i++) {
            executor.execute(new Task("任务" + i));
        }

        executor.shutdown();

        /**
         * 输出结果：
         * 无法处理的任务：Task{name='任务2'}
         * pool-1-thread-1处理任务0
         * 无法处理的任务：Task{name='任务3'}
         * 无法处理的任务：Task{name='任务4'}
         * pool-1-thread-1处理任务1
         *
         *
         * 5个任务，2个任务处理完了，3个任务进入饱和策略中，记录了任务中的日志，
         * 对于无法处理的任务，记录下来，然后后续在补偿处理。
         * 任务进入了饱和策略，说明线程池的配置可能不太合理，或者机器的性能有限，需要做一些性能优化的调整
         *
         */
    }
}