package com.juc.chat16;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * PriorityBlockingQueue优先级队列的线程池
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/24
 */
public class Demo3 {

    static class Task implements Runnable, Comparable<Task> {

        private int i;
        private String name;

        public Task(int i, String name) {
            this.i = i;
            this.name = name;
        }

        @Override
        public int compareTo(Task o) {
            return Integer.compare(o.i, this.i);
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "处理" + this.name);
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                60L, TimeUnit.SECONDS, new PriorityBlockingQueue<>());
        for (int i = 0; i < 10; i++) {
            String taskName = "任务" + i;
            executorService.execute(new Task(i, taskName));
        }

        for (int i = 100; i > 90; i--) {
            String taskName = "任务" + i;
            executorService.execute(new Task(i, taskName));
        }

        /**
         * 输出结果：
         * pool-1-thread-1处理任务0
         * pool-1-thread-1处理任务100
         * pool-1-thread-1处理任务99
         * pool-1-thread-1处理任务98
         * pool-1-thread-1处理任务97
         * pool-1-thread-1处理任务96
         * pool-1-thread-1处理任务95
         * pool-1-thread-1处理任务94
         * pool-1-thread-1处理任务93
         * pool-1-thread-1处理任务92
         * pool-1-thread-1处理任务91
         * pool-1-thread-1处理任务9
         * pool-1-thread-1处理任务8
         * pool-1-thread-1处理任务7
         * pool-1-thread-1处理任务6
         * pool-1-thread-1处理任务5
         * pool-1-thread-1处理任务4
         * pool-1-thread-1处理任务3
         * pool-1-thread-1处理任务2
         * pool-1-thread-1处理任务1
         *
         *
         * 除了第一个任务，其他任务按照优先级高低按照顺序处理。原因在于：创建线程池的时候使用了
         * 优先级队列，进入队列中的任务会进行排序，任务的先后顺序由Task中的i变量决定。
         *
         */
    }

}