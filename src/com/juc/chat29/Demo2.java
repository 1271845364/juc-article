package com.juc.chat29;

import java.sql.Time;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * 需求：使用漏桶算法来进行限流
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/15
 */
public class Demo2 {

    public static class BucketLimit {
        static AtomicInteger threadNum = new AtomicInteger();

        /**
         * 容量
         */
        private int capacity;

        /**
         * 流速
         */
        private int flowRate;

        /**
         * 流速度时间单位
         */
        private TimeUnit flowRateUnit;

        /**
         * 存放漏斗中的对象
         */
        private BlockingQueue<Node> queue;

        /**
         * 漏斗流出的任务时间间隔（纳秒）
         */
        private long flowRateNanosTime;

        public BucketLimit(int capacity, int flowRate, TimeUnit flowRateUnit) {
            this.capacity = capacity;
            this.flowRate = flowRate;
            this.flowRateUnit = flowRateUnit;
            this.bucketThreadWork();
        }

        /**
         * 漏斗线程
         */
        private void bucketThreadWork() {
            this.queue = new ArrayBlockingQueue<Node>(capacity);
            //漏斗流出的任务时间间隔（纳秒）
            this.flowRateNanosTime = flowRateUnit.toNanos(1);
            Thread thread = new Thread(() -> {
                this.bucketWork();
            });
            thread.setName("漏斗线程-" + threadNum.getAndIncrement());
            thread.start();
        }

        /**
         * 漏斗线程开始工作
         */
        private void bucketWork() {
            while (true) {
                Node node = this.queue.poll();
                if (Objects.nonNull(node)) {
                    //唤醒线程
                    LockSupport.unpark(node.thread);
                }
                //休眠flowRateNanosTime
                LockSupport.parkNanos(flowRateNanosTime);
            }
        }

        /**
         * 构建一个漏斗
         *
         * @param capacity
         * @param flowRate
         * @param flowRateUnit
         * @return
         */
        public static BucketLimit build(int capacity, int flowRate, TimeUnit flowRateUnit) {
            if (capacity < 0 || flowRate < 0) {
                throw new IllegalArgumentException("capacity、flowRate必须大于0");
            }
            return new BucketLimit(capacity, flowRate, flowRateUnit);
        }

        /**
         * 当前线程加入漏斗，返回false，表示漏斗已经满了；返回true，表示漏斗限流成功，可以继续处理任务
         *
         * @return
         */
        public boolean acquire() {
            Thread thread = Thread.currentThread();
            Node node = new Node(thread);
            if (this.queue.offer(node)) {
                LockSupport.park();
                return true;
            }
            return false;
        }

        /**
         * 漏斗中的元素
         */
        class Node {
            private Thread thread;
            public Node(Thread thread) {
                this.thread = thread;
            }
        }
    }

    public static void main(String[] args) {
        BucketLimit bucketLimit = BucketLimit.build(10, 60, TimeUnit.MINUTES);
        for (int i = 0; i < 15; i++) {
            new Thread(() -> {
                boolean acqurire = bucketLimit.acquire();
                System.out.println(System.currentTimeMillis() + " " + acqurire);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}