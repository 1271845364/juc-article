package com.juc.chat16;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * SynchronousQueue队列的线程池
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/23
 */
public class Demo2 {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 50; i++) {
            String taskName = "任务" + i;
            executorService.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "处理" + taskName);
                try {
                    //模拟任务内部耗时
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();

        /**
         * 输出结果：
         * pool-1-thread-1处理任务0
         * pool-1-thread-2处理任务1
         * pool-1-thread-3处理任务2
         * pool-1-thread-4处理任务3
         * pool-1-thread-5处理任务4
         * pool-1-thread-6处理任务5
         * pool-1-thread-7处理任务6
         * pool-1-thread-8处理任务7
         * pool-1-thread-9处理任务8
         * pool-1-thread-10处理任务9
         * pool-1-thread-11处理任务10
         * pool-1-thread-12处理任务11
         * pool-1-thread-13处理任务12
         * pool-1-thread-14处理任务13
         * pool-1-thread-15处理任务14
         * pool-1-thread-16处理任务15
         * pool-1-thread-18处理任务17
         * pool-1-thread-19处理任务18
         * pool-1-thread-20处理任务19
         * pool-1-thread-21处理任务20
         * pool-1-thread-22处理任务21
         * pool-1-thread-23处理任务22
         * pool-1-thread-24处理任务23
         * pool-1-thread-25处理任务24
         * pool-1-thread-17处理任务16
         * pool-1-thread-42处理任务41
         * pool-1-thread-28处理任务27
         * pool-1-thread-27处理任务26
         * pool-1-thread-29处理任务28
         * pool-1-thread-30处理任务29
         * pool-1-thread-31处理任务30
         * pool-1-thread-32处理任务31
         * pool-1-thread-33处理任务32
         * pool-1-thread-34处理任务33
         * pool-1-thread-35处理任务34
         * pool-1-thread-36处理任务35
         * pool-1-thread-37处理任务36
         * pool-1-thread-38处理任务37
         * pool-1-thread-39处理任务38
         * pool-1-thread-40处理任务39
         * pool-1-thread-26处理任务25
         * pool-1-thread-41处理任务40
         * pool-1-thread-50处理任务49
         * pool-1-thread-46处理任务45
         * pool-1-thread-45处理任务44
         * pool-1-thread-48处理任务47
         * pool-1-thread-47处理任务46
         * pool-1-thread-49处理任务48
         * pool-1-thread-43处理任务42
         * pool-1-thread-44处理任务43
         *
         * public static ExecutorService newCachedThreadPool() {
         *         return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
         *                                       60L, TimeUnit.SECONDS,
         *                                       new SynchronousQueue<Runnable>());
         * }
         *
         * 创建了50个线程处理任务，使用了SynchronousQueue同步队列，这种队列比较特殊，放入元素必须要有另外一个线程去
         * 获取这个元素，否则放入元素会失败或者一直阻塞在那里直到有线程取走，代码中休眠了1s，导致已经创建的线程都处于处理任务
         * 状态，所以新来的任务丢入到同步队列会失败，丢入到同步队列失败之后，就会尝试新建线程处理任务。
         * 使用上面的方式创建线程池需要注意，如果需要处理的任务比较耗时，会导致新来的任务都会创建新的线程进行处理，
         * 可能会导致创建非常多的线程，最终耗尽系统资源，导致OOM
         *
         *
         *
         */
    }
}