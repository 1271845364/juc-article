package com.juc.chat16;

import java.util.concurrent.*;

/**
 * beforeExecute：任务执行之前调用的方法，有2个参数，第1个参数是执行任务的线程，第2个参数是任务
 * <p>
 * protected void beforeExecute(Thread t, Runnable r) { }
 * afterExecute：任务执行完成之后调用的方法，2个参数，第1个参数表示任务，第2个参数表示任务执行时的异常信息，如果无异常，第二个参数为null
 * <p>
 * protected void afterExecute(Runnable r, Throwable t) { }
 * terminated：线程池最终关闭之后调用的方法。所有的工作线程都退出了，最终线程池会退出，退出时调用该方法
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/24
 */
public class Demo6 {

    static class Task implements Runnable {

        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "处理" + this.name);
            try {
                TimeUnit.SECONDS.sleep(2);
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

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), Executors.defaultThreadFactory(), (r, executors) -> {
            //自定义饱和策略
            //记录一下无法处理的任务
            System.out.println("无法处理的任务：" + r.toString());
        }) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println(System.currentTimeMillis() + "，" + t.getName() + "，开始执行任务：" + t.toString());
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "，任务：" + r.toString() + "，执行完毕！");
            }

            @Override
            protected void terminated() {
                System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "关闭线程池！");
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.execute(new Task("任务" + i));
        }

        TimeUnit.SECONDS.sleep(1);
        executor.shutdown();

        /**
         * 输出结果：
         * 1569326405402，pool-1-thread-1，开始执行任务：Thread[pool-1-thread-1,5,main]
         * pool-1-thread-1处理任务0
         * 1569326405402，pool-1-thread-2，开始执行任务：Thread[pool-1-thread-2,5,main]
         * pool-1-thread-2处理任务1
         * 1569326405402，pool-1-thread-3，开始执行任务：Thread[pool-1-thread-3,5,main]
         * pool-1-thread-3处理任务2
         * 1569326405402，pool-1-thread-4，开始执行任务：Thread[pool-1-thread-4,5,main]
         * pool-1-thread-4处理任务3
         * 1569326405402，pool-1-thread-5，开始执行任务：Thread[pool-1-thread-5,5,main]
         * pool-1-thread-5处理任务4
         * 1569326405402，pool-1-thread-6，开始执行任务：Thread[pool-1-thread-6,5,main]
         * pool-1-thread-6处理任务5
         * 1569326405402，pool-1-thread-7，开始执行任务：Thread[pool-1-thread-7,5,main]
         * 1569326405402，pool-1-thread-8，开始执行任务：Thread[pool-1-thread-8,5,main]
         * pool-1-thread-8处理任务7
         * pool-1-thread-7处理任务6
         * 1569326405402，pool-1-thread-9，开始执行任务：Thread[pool-1-thread-9,5,main]
         * pool-1-thread-9处理任务8
         * 1569326405402，pool-1-thread-10，开始执行任务：Thread[pool-1-thread-10,5,main]
         * pool-1-thread-10处理任务9
         * 1569326407418，pool-1-thread-2，任务：Task{name='任务1'}，执行完毕！
         * 1569326407418，pool-1-thread-7，任务：Task{name='任务6'}，执行完毕！
         * 1569326407418，pool-1-thread-10，任务：Task{name='任务9'}，执行完毕！
         * 1569326407418，pool-1-thread-9，任务：Task{name='任务8'}，执行完毕！
         * 1569326407418，pool-1-thread-6，任务：Task{name='任务5'}，执行完毕！
         * 1569326407418，pool-1-thread-8，任务：Task{name='任务7'}，执行完毕！
         * 1569326407418，pool-1-thread-5，任务：Task{name='任务4'}，执行完毕！
         * 1569326407418，pool-1-thread-4，任务：Task{name='任务3'}，执行完毕！
         * 1569326407418，pool-1-thread-3，任务：Task{name='任务2'}，执行完毕！
         * 1569326407418，pool-1-thread-1，任务：Task{name='任务0'}，执行完毕！
         * 1569326407418，pool-1-thread-1关闭线程池！
         *
         * 从输出结果看，每个需要执行的任务打印了3行日志，执行前由线程池的beforeExecute打印
         * 执行时会调用任务的run方法，任务执行完毕之后，会调用线程池的afterExecute方法，从每个
         * 任务的首尾2条日志中可以看到每个任务耗时2秒左右。线程池最终关闭调用terminated方法
         *
         *
         * 合理地配置线程池
         * 1、任务的性质：CPU密集型任务、IO密集型任务和混合型任务
         * 2、任务的优先级：高、中、低
         * 3、任务的执行时间：长、中、短
         * 4、任务的依赖性：是否依赖其他的系统资源，如数据库连接
         * 性质不同的任务可以用不同规模的线程池分开处理。CPU密集型任务应该尽可能小的线程，
         * 如配置cpu数量+1个线程的线程池。
         * 由于IO密集型任务并不是一直在执行任务，不能让CPU闲着，则应配置尽可能多的线程，如：cpu数量*2
         * 混合型任务，如果可以拆分，将其拆分成一个CPU密集型任务和一个IO密集型任务，只要这2个任务执行的时间
         * 相差不是太大，那么分解后执行的吞吐量将高于串行执行的吞吐量。可以通过Runtime.getRuntime().availableProcessors()
         * 方法获取CPU数量。优先级不同任务可以对线程池采用优先级队列来处理，让优先级高的先执行。
         *
         *
         * 使用队列的时候建议使用有界队列，有界队列增加了系统的稳定性，如果采用无界队列，
         * 任务太多的时候可能导致系统OOM，直接让系统宕机
         *
         *
         * 线程池中线程数量的配置
         * 线程池中总线程大小对系统的性能有一定的影响，我们的目标是系统能够发挥最好的性能，过多或者过小的线程
         * 数量都无法有效的使用机器的性能。
         * 线程池大小估算公式：
         *  Ncpu = cpu数量
         *  Ucpu = 目标cpu使用率，0<=Ucpu<=1
         *  W/C = 等待时间与计算时间的比例
         *  为保存处理器达到期望的使用率，最优的线程池大小 = Nthreads = Ncpu * Ucpu * (1 + W/C)
         *
         *
         *
         *
         */
    }

}