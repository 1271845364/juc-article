package com.juc.chat09;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同一个锁支持创建多个Condition
 * 使用两个Condition来实现一个阻塞队列
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/10
 */
public class BlockQueueDemo<E> {

    /**
     * 阻塞队列最大容量
     */
    private int size;

    private Lock lock = new ReentrantLock();

    /**
     * 队列底层实现
     */
    private List<E> list = new LinkedList<>();

    /**
     * 队列满时的等待条件
     */
    private Condition notFull = lock.newCondition();

    /**
     * 队列空时的等待条件
     */
    private Condition notEmpty = lock.newCondition();

    public BlockQueueDemo(int size) {
        this.size = size;
    }

    /**
     * 入队
     *
     * @param e
     * @throws InterruptedException
     */
    public void enqueue(E e) throws InterruptedException {
        lock.lock();
        try {
            //队列满了
            while (list.size() == size) {
                notFull.await();
            }
            //入队，进入链表末尾
            list.add(e);
            System.out.println("入队:" + e);
            //通知在notEmpty条件上的等待的线程
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 出队
     *
     * @return
     */
    public E dequeue() throws InterruptedException {
        E e;
        lock.lock();
        try {
            //队列为空，在notEmpty上等待
            while (list.size() == 0) {
                notEmpty.await();
            }
            //出队，移除链表首元素
            e = list.remove(0);
            System.out.println("出队：" + e);
            //通知在notEmpty条件上等待的线程
            notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BlockQueueDemo<Integer> queue = new BlockQueueDemo<>(2);
        for (int i = 0; i < 100; i++) {
            int data = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        queue.enqueue(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i = 0; i < 100; i++) {
            int data = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        queue.dequeue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        /**
         * 创建了一个阻塞队列，大小为2，队列满的时候，会被阻塞，等待其他线程去消费，队列中的元素被消费之后，
         * 会唤醒生产者，生产数据进入队列。上面的代码将队列大小设置为1，可以实现同步阻塞队列，生产1个元素之后，生产者
         * 会被阻塞，待消费者消费队列中的元素之后，生产者才能继续生产数据
         *
         *
         * 1、使用condition步骤：创建Condition对象，获取锁，然后调用condition方法
         * 2、一个ReentrantLock支持多个Condition对象
         * 3、void await() throws InterruptedException；方法会释放锁，让当前线程等待，支持被其他线程唤醒，支持线程中断
         * 4、void awaitUniterruptibly();方法会释放锁，让当前线程等待，支持被其他线程唤醒，不支持线程中断
         * 5、long awaitNanos(long nanosTime) throws InterruptedException;参数为纳秒，此方法会释放锁，让当前线程等待，支持被
         *    其他线程唤醒，支持线程中断。超时之后返回的，结果为负数，超时之前返回的，结果为正数(表示返回距离超时时间相差的纳秒数)
         * 6、boolean await(long time,TimeUnit unit) throws InterruptedException;方法会释放锁，让当前线程等待，支持被其他线程唤醒
         *    支持中断。超时之后返回的，结果为false；超时之前被唤醒返回的，结果为true
         * 7、boolean awaitUntil(Date deadline) throws InterruptedException;参数表示超时的截止时间点，方法会释放锁，让当前线程等待，支持唤醒，
         *    支持中断。超时之后返回的，结果为false；超时之前返回的，结果为true
         * 8、void signal();会唤醒一个等待的线程，然后被唤醒的线程会被加入到同步队列，去尝试获取锁
         * 9、void signalAll();会唤醒所有等待的线程，然后所有等待的线程加入同步队列，然后去尝试获取锁
         *
         */

    }
}