package com.juc.chat25;

import java.util.Calendar;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue是一个支持延迟获取元素的无界阻塞队列，里面的元素全部都是"可延期"的元素，如果队列里面没有元素到期，是不能从
 * 队列头部获取元素的，哪怕有元素也不行，也就是说只有延迟期到时才能够从队列中取元素
 *
 * DelayQueue<E extends Delayed> extends AbstractQueue<E>
 * implements BlockingQueue<E>
 * 元素E需要实现接口Delayed
 * public interface Delayed extends Comparable<Delayed>{
 * long getDelay(TimeUnit unit);
 * }
 * Delayed继承了Comparable接口，这个接口是用来做比较用的，DelayQueue内部使用PriorityQueue来存储数据的，PriorityQueue是一个
 * 优先级队列，丢入的数据会进行排序，排序的方法调用的是Comparable接口中的方法。
 *
 * 需求：推送业务，有时候我们希望早上9点或者其他时间进行推送
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/12
 */
public class Demo4 {

    /**
     * 封装推送消息
     */
    private static class Msg implements Delayed {

        /**
         * 优先级，越小越优先
         */
        private int priority;

        /**
         * 消息内容
         */
        private String msg;

        /**
         * 定时发送，毫秒格式
         */
        private long sendTimeMs;

        public Msg(int priority, String msg, long sendTimeMs) {
            this.priority = priority;
            this.msg = msg;
            this.sendTimeMs = sendTimeMs;
        }

        @Override
        public String toString() {
            return "Msg{" +
                    "priority=" + priority +
                    ", msg='" + msg + '\'' +
                    ", sendTimeMs=" + sendTimeMs +
                    '}';
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(sendTimeMs - Calendar.getInstance().getTimeInMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (o instanceof Msg) {
                Msg msg = (Msg) o;
                return Integer.compare(this.priority, msg.priority);
            }
            return 0;
        }
    }

    /**
     * 推送队列
     */
    private static DelayQueue<Msg> pushQueue = new DelayQueue<>();

    static {
        //启动一个线程进行推送
        new Thread(() -> {
            while (true) {
                Msg msg;
                try {
                    msg = pushQueue.take();
                    long endTime = System.currentTimeMillis();
                    System.out.println(String.format("定时发送时间：%s,实际发送时间：%s,发送消息：%s", msg.sendTimeMs,
                            endTime, msg.msg));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 推送消息，需要推送的消息放到延迟队列
     *
     * @param priority
     * @param msg
     * @param sendTimeMs
     */
    private static void pushMsg(int priority, String msg, long sendTimeMs) {
        pushQueue.put(new Msg(priority, msg, sendTimeMs));
    }

    public static void main(String[] args) {
        for (int i = 5; i > 0; i--) {
            String msg = "学习java高并发，第" + i + "天";
            Demo4.pushMsg(i, msg, Calendar.getInstance().getTimeInMillis() + i * 2000);
        }

        /**
         * 输出结果：
         * 定时发送时间：1570881924022,实际发送时间：1570881924025,发送消息：学习java高并发，第1天
         * 定时发送时间：1570881926022,实际发送时间：1570881926024,发送消息：学习java高并发，第2天
         * 定时发送时间：1570881928022,实际发送时间：1570881928024,发送消息：学习java高并发，第3天
         * 定时发送时间：1570881930022,实际发送时间：1570881930035,发送消息：学习java高并发，第4天
         * 定时发送时间：1570881932006,实际发送时间：1570881932009,发送消息：学习java高并发，第5天
         *
         * 从上面可以看出秒级别上，定时发送和实际发送的时间相等，代码中Msg显示Delayed接口，重点在于getDelay方法，这个
         * 方法返回剩余的延迟时间，代码中使用this.sendTimeMs减去当前时间的毫秒格式时间，得到剩余延迟时间
         *
         */
    }


    /**
     * LinkedTransferQueue是一个由链表结构组成的无界的阻塞TransferQueue队列。相对于其他的阻塞队列，LinkedTransferQueue多了tryTransfer
     * 和transfer方法
     * LinkedTransferQueue类继承自AbstractQueue抽象类，并且实现了TransferQueue接口：
     * public interface TransferQueue<E> extends BlockingQueue<E> {
     *     // 如果存在一个消费者已经等待接收它，则立即传送指定的元素，否则返回false，并且不进入队列。
     *     boolean tryTransfer(E e);
     *     // 如果存在一个消费者已经等待接收它，则立即传送指定的元素，否则等待直到元素被消费者接收。
     *     void transfer(E e) throws InterruptedException;
     *     // 在上述方法的基础上设置超时时间
     *     boolean tryTransfer(E e, long timeout, TimeUnit unit)
     *         throws InterruptedException;
     *     // 如果至少有一位消费者在等待，则返回true
     *     boolean hasWaitingConsumer();
     *     // 获取所有等待获取元素的消费线程数量
     *     int getWaitingConsumerCount();
     * }
     * transfer(E e)和SynchronousQueue中的put方法类似，都需要等待消费者消费取走元素，否则一直等待。其他方法和ArrayBlockingQueue、LinkedBlockingQueue
     * 中的方法类似
     *
     */

}