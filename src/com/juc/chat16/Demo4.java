package com.juc.chat16;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义创建线程工厂
 * <p>
 * 需要实现java.util.ThreadFactory接口中的Thread.newThread(Runnable r)方法，参数为传入的任务，需要返回一个工作线程
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/24
 */
public class Demo4 {

    private static AtomicInteger threadNum = new AtomicInteger(1);

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 60L,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), r -> {
            Thread thread = new Thread(r);
            thread.setName("自定义线程-" + threadNum.getAndIncrement());
            return thread;
        });

        for (int i = 0; i < 5; i++) {
            String taskName = "任务" + i;
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "处理" + taskName);
            });
        }
        executor.shutdown();


        /**
         * 输出结果：
         * 自定义线程-1处理任务0
         * 自定义线程-4处理任务3
         * 自定义线程-3处理任务2
         * 自定义线程-2处理任务1
         * 自定义线程-5处理任务4
         *
         * 代码中在任务中输出了当前线程的名称，看到的是自定义的名称
         * 注释掉executor.shutdown();
         * 通过jstack查看线程的堆栈信息，也可以看到我们自定义的名称，我们可以将代码中的
         * executor.shutdown();注释掉，线程不退出
         * C:\Users\Administrator>jstack 3856
         * 2019-09-24 19:12:41
         * Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.66-b17 mixed mode):
         *
         * "DestroyJavaVM" #18 prio=5 os_prio=0 tid=0x0000000002bb2800 nid=0xb20 waiting on condition [0x0000000000000000]
         *    java.lang.Thread.State: RUNNABLE
         *
         * "自定义???程-5" #17 prio=5 os_prio=0 tid=0x0000000019176000 nid=0x304c waiting on condition [0x000000001a6be000]
         *    java.lang.Thread.State: WAITING (parking)
         *         at sun.misc.Unsafe.park(Native Method)
         *         - parking to wait for  <0x00000000d5eb6cf8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
         *         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
         *         at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
         *         at java.util.concurrent.ArrayBlockingQueue.take(ArrayBlockingQueue.java:403)
         *         at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1067)
         *         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1127)
         *         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
         *         at java.lang.Thread.run(Thread.java:745)
         *
         * "自定义线程-4" #16 prio=5 os_prio=0 tid=0x0000000019175000 nid=0x7cc waiting on condition [0x000000001a5bf000]
         *    java.lang.Thread.State: WAITING (parking)
         *
         *
         *
         *
         *
         */

    }

}