package com.juc.chat25;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * SynchronousQueue同步阻塞队列，没有容量，与其他的BlockingQueue不同，SynchronousQueue是一个不存储元素的BlockingQueue，每一个put操作必须
 * 要等待一个take操作，否则不能继续添加元素，反之亦然。SynchronousQueue在现实中用的不多，线程池中有用到过，Executors.newCachedThreadPool()
 * 实现用到了这个队列，当有任务丢入线程池的时候，如果已创建的工作线程都在忙于处理任务，则会新建一个线程来处理丢入队列的任务
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/11
 */
public class Demo3 {

    private static SynchronousQueue<String> queue = new SynchronousQueue<>();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                queue.put("学习java高并发");
                long endTime = System.currentTimeMillis();
                System.out.println(String.format("%s,%s,take耗时：%s ms,%s", startTime, endTime,
                        endTime - startTime, Thread.currentThread().getName()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //休眠5s之后，从队列中take一个元素
        TimeUnit.SECONDS.sleep(5);
        System.out.println(System.currentTimeMillis() + ",调用take获取并移除元素：" + queue.take());

        /**
         * 输出结果：
         * 1570795372197,调用take获取并移除元素：学习java高并发
         * 1570795367197,1570795372197,take耗时：5000,Thread-0
         *
         * main方法中启动了一个线程，调用queue.put方法向队列中丢入一条数据，调用的时候产生了阻塞，从输出结果中可以看出，直到take方法被调用时候，
         * put方法才从阻塞状态恢复正常
         *
         */
    }

}