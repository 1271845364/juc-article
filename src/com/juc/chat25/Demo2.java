package com.juc.chat25;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * LinkedBlockingQueue：内部使用单向链表实现的阻塞队列
 * //默认构造器，容量大小为Integer.MAX_VALUE
 * public LinkedBlockingQueue()
 * //创建指定大小的LinkedBlockingQueue
 * public LinkedBlockingQueue(int capacity)
 * //容量大小为Integer.MAX_VALUE，并将传入的集合放到队列中
 * public LinkedBlockingQueue(Collection<? extends E> c)
 * LinkedBlockingQueue和ArrayBlockingQueue的使用类似，建议使用指定容量大小的，如果不指定容量大小，插入的太快，消费的太慢，会导致OOM
 * <p>
 * PriorityBlockingQueue无界的优先级的阻塞队列，内部使用数组存储数据，达到容量时，会自动进行扩容，放入的元素按照优先级进行排序
 * //默认的构造器，初始化容量大小为11
 * public PriorityBlockingQueue()
 * //指定队列的初始化容量大小
 * public PriorityBlockingQueue(int initialCapacity)
 * //指定队列的初始化容量大小和放入元素的比较器
 * public PriorityBlockingQueue(int initialCapacity,Comparator<? super E> comparator)
 * //传入集合来初始化队列，传入的集合可以实现SortedSet接口或PriorityQueue接口进行排序，如果没有实现这2个接口，按照正常顺序放入队列
 * public PriorityBlockingQueue(Collection<? extends E> c)
 * 优先级队列放入元素的时候，会进行排序，所以我们需要指定排序规则，有2种方式
 * 1、创建PriorityBlockingQueue的时候指定比较器comparator
 * 2、放入元素需要实现Comparator接口
 * 上面2种方式必须选一个，如果2个都有，则走第一个规则排序。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/11
 */
public class Demo2 {

    /**
     * 需求：目前的推送是按照放入的先后顺序进行发送的，比如有些公告比较紧急，优先级比较高 ，需要快点发送，怎么搞？
     */

    /**
     * 推送信息封装
     */
    static class Msg implements Comparable<Msg> {

        /**
         * 越小的优先级越高
         */
        private int priority;

        /**
         * 推送的消息
         */
        private String msg;

        public Msg(int priority, String msg) {
            this.priority = priority;
            this.msg = msg;
        }

        @Override
        public int compareTo(Msg o) {
            return Integer.compare(this.priority, o.priority);
        }

        @Override
        public String toString() {
            return "Msg{" +
                    "priority=" + priority +
                    ", msg='" + msg + '\'' +
                    '}';
        }

    }

    /**
     * 推送队列
     */
    private static PriorityBlockingQueue<Msg> pushQueue = new PriorityBlockingQueue<>();

    static {
        new Thread(() -> {
            while (true) {
                Msg msg;
                try {
                    long startTime = System.currentTimeMillis();
                    //从消息队列中获取一条消息，此方法会进行阻塞，直到返回结果
                    msg = pushQueue.take();

                    //模拟推送耗时
                    TimeUnit.MILLISECONDS.sleep(100);
                    long endTime = System.currentTimeMillis();
                    System.out.println(String.format("[%s,%s,take耗时：%s],%s,发送消息：%s",
                            startTime, endTime, endTime - startTime, Thread.currentThread().getName(), msg));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        for (int i = 5; i > 0; i--) {
            String msg = "一起学习java高并发，第" + i + "天";
            Demo2.pushMsg(msg, i);
        }

        /**
         * 输出结果：
         * [1570794220776,1570794220878,take耗时：102],Thread-0,发送消息：Msg{priority=4, msg='一起学习java高并发，第4天'}
         * [1570794220928,1570794221028,take耗时：100],Thread-0,发送消息：Msg{priority=1, msg='一起学习java高并发，第1天'}
         * [1570794221028,1570794221129,take耗时：101],Thread-0,发送消息：Msg{priority=2, msg='一起学习java高并发，第2天'}
         * [1570794221129,1570794221229,take耗时：100],Thread-0,发送消息：Msg{priority=3, msg='一起学习java高并发，第3天'}
         * [1570794221229,1570794221329,take耗时：100],Thread-0,发送消息：Msg{priority=5, msg='一起学习java高并发，第5天'}
         *
         * main中放了5条推送消息，i作为消息的优先级按照倒序放入的，最终输出结果中按照优先级由小到大输出。Msg实现了Comparable接口，具有了比较功能
         *
         *
         */
    }

    /**
     * 推送消息
     *
     * @param msg
     * @param i
     */
    private static void pushMsg(String msg, int i) {
        pushQueue.put(new Msg(i, msg));
    }
}