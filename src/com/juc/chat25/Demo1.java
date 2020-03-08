package com.juc.chat25;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * BlockingQueue接口
 * BlockingQueue位于juc中，熟称阻塞队列， 阻塞队列首先它是一个队列，继承Queue接口，是队列就会遵循先进先出（FIFO）的原则，又因为它是阻塞的，故与普通的队列有两点区别：
 * 当一个线程向队列里面添加数据时，如果队列是满的，那么将阻塞该线程，暂停添加数据
 * 当一个线程从队列里面取出数据时，如果队列是空的，那么将阻塞该线程，暂停取出数据
 *
 * BlockingQueue相关方法：
 * 操作类型	抛出异常	 返回特殊值	一直阻塞	超时退出
 * 插入	    add(e)	offer(e)	put(e)	offer(e,timeuout,unit)
 * 移除	    remove()	poll()	take()	poll(timeout,unit)
 * 检查	    element()	peek()	不支持	不支持
 * 重点，再来解释一下，加深印象：
 * 3个可能会有异常的方法，add、remove、element；这3个方法不会阻塞（是说队列满或者空的情况下是否会阻塞）；队列满的情况下，add抛出异常；队列为空情况下，remove、element抛出异常
 * offer、poll、peek 也不会阻塞（是说队列满或者空的情况下是否会阻塞）；队列满的情况下，offer返回false；队列为空的情况下，pool、peek返回null
 * 队列满的情况下，调用put方法会导致当前线程阻塞
 * 队列为空的情况下，调用take方法会导致当前线程阻塞
 * offer(e,timeuout,unit)，超时之前，插入成功返回true，否者返回false
 * poll(timeout,unit)，超时之前，获取到头部元素并将其移除，返回true，否者返回false
 *
 * ArrayBlockingQueue：基于数组的消息队列实现，其内部维护了一个定长的数组，用于存储元素。线程阻塞的实现是通过ReentrantLock来完成的，数据的插入与取出
 * 共用同一个锁，因此ArrayBlockingQueue并不能实现生产、消费同时进行。而且在创建ArrayBlockingQueue时，我们还可以控制对象内部锁是否采用公平锁，默认是非公平锁
 *
 * LinkedBlockingQueue：基于单项链表的阻塞队列实现，在初始化LinkedBlockingQueue的时候可以指定大小，也可以不指定大小，默认类似于无限大小的
 * 容量(Integer.MAX_VALUE)，不指定队列容量大小也是会有风险的，一旦数据生产速度大于消费速度，系统内存将有可能被消耗殆尽，因此要谨慎操作。另外
 * LinkedBlockingQueue中用于阻塞生产者、消费者的锁是两个（锁分离），因此生产和消费是可以同时进行
 *
 * PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列，进入队列的元素会按照优先级进行排序
 *
 * SynchronousQueue：同步阻塞队列，SynchronousQueue没有容量，与其他BlockingQueue不同，SynchronousQueue是一个不存储元素的BlockingQueue，
 * 每一个put操作必须要等待一个take操作，否则不能继续添加元素，反之亦然
 *
 * DelayQueue：是一个支持延迟获取元素的无界阻塞队列，里面的元素全部都是"可延期"的元素，列头的元素最先"到期"的元素，如果队列里面没有元素到期，是不能
 * 从队列头获取元素的，哪怕有元素也不行，也就是说只有延迟到期时才能够从队列中获取元素
 *
 * LinkedTransferQueue：是基于链表的FIFO无界阻塞队列，他出现在JDK1.7中，Doug Lea 大神说LinkedTransferQueue是一个聪明的队列，
 * 它是ConcurrentLinkedQueue、SynchronousQueue(公平模式下)、无界的LinkedBlockingQueues等的超集，
 * LinkedTransferQueue包含了ConcurrentLinkedQueue、SynchronousQueue、LinkedBlockingQueues三种队列的功能
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/10
 */
public class Demo1 {

    /**
     * 需求：业务系统中有很多地方需要推送通知，由于需要推送的数据太多，我们将需要推送的消息先丢到阻塞队列中，然后开一个线程进行处理真实发送
     */
    //推送队列
    private static ArrayBlockingQueue<String> pushQueue = new ArrayBlockingQueue<>(10000);


    static {
        //启动一个线程做真实推送
        new Thread(() -> {
            while (true) {
                String msg;
                try {
                    long startTime = System.currentTimeMillis();
                    //获取一条推送消息，此方法会进行阻塞，直到返回结果
                    msg = pushQueue.take();
                    long endTime = System.currentTimeMillis();

                    //模拟推送耗时
                    TimeUnit.MILLISECONDS.sleep(500);

                    System.out.println(String.format("[%s,%s,take耗时：%s],%s,发送消息：%s",
                            startTime, endTime, endTime - startTime, Thread.currentThread().getName(), msg));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 推送消息，需要发送推送消息的调用方法，会将推送信息先加入推送队列
     *
     * @param msg
     */
    private static void pushMsg(String msg) throws InterruptedException {
        pushQueue.put(msg);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            String msg = "一起来学习java高并发，第" + i + "天";
            //模拟耗时
            TimeUnit.SECONDS.sleep(i);
            Demo1.pushMsg(msg);
        }

        /**
         * 输出结果：
         * [1570790820071,1570790820071,take耗时：0],Thread-0,发送消息：一起来学习java高并发，第0天
         * [1570790820618,1570790821072,take耗时：454],Thread-0,发送消息：一起来学习java高并发，第1天
         * [1570790821572,1570790823072,take耗时：1500],Thread-0,发送消息：一起来学习java高并发，第2天
         * [1570790823572,1570790826072,take耗时：2500],Thread-0,发送消息：一起来学习java高并发，第3天
         * [1570790826573,1570790830072,take耗时：3499],Thread-0,发送消息：一起来学习java高并发，第4天
         *
         * 使用了有界队列ArrayBlockingQueue，创建ArrayBlockingQueue的时候需要指定容量大小，调用push.put将推送消息放入到队列中，如果队列已经满了，
         * 会阻塞。static块中启动了一个线程，调用push.take从队列中获取等待的推送消息进行推送处理
         *
         * 注意：如果容量太小，消费者发送太快，消费者消费太慢，会导致队列空间满，调用put方法会导致线程阻塞，合理设置大小很重要
         *
         */
    }

}