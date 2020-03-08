package com.juc.chat18;

import java.util.concurrent.*;

/**
 * Executors框架包括：
 * Executor：方法execute(Runnable runnable)接口，执行任务runnable
 * ExecutorService：继承于Executor接口，有三种状态：运行、关闭、终止，创建后进入运行状态，调用shutdown便进入关闭状态，此时不再接受新的任务，但是会
 * 执行完已经提交的任务，已经提交完的任务都执行完就达到终止状态。如果不调用shutdown方法，会一直运行下去，系统不会主动关闭
 * ThreadPoolExecutor：实现ExecutorService接口中所有的方法，非常重要的类
 * ScheduledThreadPoolExecutor：继承ThreadPoolExecutor，延迟执行任务或者定时执行任务。功能和Timer类似，但是ScheduledThreadPoolExecutor
 * 更强大、更灵活，Timer后台是单线程，ScheduledThreadPoolExecutor可以在创建的时候指定多个线程
 * Executors：提供了一系列工厂方法用于创建线程池，返回的线程池都实现了ExecutorService接口
 *            public static ExecutorService newSingleThreadExecutor()
 *            public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory)
 *            创建单一的线程池，这个线程池中只有一个线程在工作，也就是相当于当线程串行执行所有任务。如果这个唯一的线程因为异常结束，
 *            那么会有一个新的线程来替代它。此线程池保证所有的任务执行顺序按照任务的提交顺序执行，内部使用了无限容量的LinkedBlockingQueue
 *            阻塞队列来缓存任务，任务如果比较多，单线程如果处理不过来，会导致队列堆满，导致OOM
 *
 *            public static ExecutorService newFixedThreadPool(int nThreads)
 *            public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory)
 *            固定大小的线程池，每次提交一个任务就创建一个线程，直到线程达到最大线程池的最大大小。线程池的大小一旦达到最大值就会保持不变，
 *            在提交新任务，任务将会进入等待队列中等待。如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。内部使用了无限容量的
 *            LinkedBlockingQueue阻塞队列来缓存任务，任务如果比较多，如果处理不过来，会导致队列堆满，导致OOM
 *
 *            public static ExecutorService newCachedThreadPool()
 *            public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory)
 *            创建一个可以缓存的线程池。如果线程池大小超过了处理任务所需要的线程，
 *            那么就会回收部分空闲(60s处于等待任务到来)的线程，当任务增加的时，此线程池又可以智能的添加
 *            新线程来处理任务。此线程池最大值是Integer的最大值(2^31-1)。内部使用了SynchronousQueue同步队列来缓存任务，此队列的
 *            特性是放入任务时必须要有对应的线程获取任务，任务才可以放入成功。如果处理的任务比较耗时，任务来的速度也比较快，会创建比较多的
 *            线程引发OOM
 *
 *            public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
 *            public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory)
 *            线程资源必须通过线程池提供，不允许在应用中自行显示的创建线程，线程使用更加的规范，合理控制线程的数量，
 *            另一方面线程的细节管理交给线程池处理，优化了资源的开销。而线程池不允许使用Executors去创建，而要通过ThreadPoolExecutor方式
 *            ，Executors可以创建线程池，但是有局限性，不够灵活；另外Executors去创建的实际上底层也是ThreadPoolExecutor方式实现的，
 *            使用ThreadPoolExecutor有助于明确线程池的运行规则，创建符合自己的业务场景需要的线程池，避免资源耗尽
 * Future：定义了异步操作任务的一些方法，获取异步任务执行结果、取消任务的执行、任务是否被取消、任务执行是否完毕
 *         boolean cancel(boolean mayInterruptIfRunning); 取消在执行的任务，参数表示是否对执行的任务发送中断信号，
 *         boolean isCancelled(); 用来判断任务是否被取消
 *         boolean isDone(); 判断任务是否执行完毕
 * Callable：主要用于子线程去执行任务，主线程去干别的事情去了，子线程执行的任务处理比较耗时，没必要主线程等着，过了一会获取子线程的执行结果
 * FutureTask
 * CompletableFuture
 * CompletionService
 * ExecutorCompletionService
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/26
 */
public class Demo1 {

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.schedule(() -> {
            System.out.println(System.currentTimeMillis() + "开始执行");
            //模拟任务耗时
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis() + "执行结束");
        }, 2, TimeUnit.SECONDS);

        /**
         * 输出结果：
         * 1569484189085
         * 1569484191143开始执行
         * 1569484194150执行结束
         *
         * 延迟2s执行
         */
    }
}