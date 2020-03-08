package com.juc.chat09;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition能够支持不响应中断，而通过使用Object方式不支持
 * Condition能够支持多个等待队列（new 多个Condition对象），而Object方式只能支持一个
 * Condition能够支持超时时间的设置，而Object不支持
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/06
 */
public class Demo2 {

    static ReentrantLock lock = new ReentrantLock();

    static Condition condition = lock.newCondition();

    public static class T1 extends Thread {
        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 准备获取锁");
            lock.lock();
            try {
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 获取锁成功");
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 释放锁成功");
        }
    }

    public static class T2 extends Thread {
        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 准备获取锁");
            lock.lock();
            try {
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 获取锁成功");
                condition.signal();
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " signal");
                TimeUnit.SECONDS.sleep(5);
                System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 准备释放锁");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println(System.currentTimeMillis() + ":" + this.getName() + " 释放锁成功");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T1 t1 = new T1();
        t1.setName("t1");
        t1.start();

        TimeUnit.SECONDS.sleep(5);

        T2 t2 = new T2();
        t2.setName("t2");
        t2.start();

        /**
         * 输出结果：
         * 1567774710135:t1 准备获取锁
         * 1567774710135:t1 获取锁成功
         * 1567774715137:t2 准备获取锁
         * 1567774715137:t2 获取锁成功
         * 1567774715138:t2 signal
         * 1567774720139:t2 准备释放锁
         * 1567774720139:t2 释放锁成功
         * 1567774720139:t1 释放锁成功
         *
         * 输出结果和使用synchronized是一样的
         * Condition.await()方法和Object的wait()方法类似，当使用Condition.await()方法时，需要先获取
         * Condition关联的对象ReentrantLock的锁，在Condition.await()方法被调用的时，当前线程会释放这个锁，
         * 并且当前线程会进行等待(处于阻塞状态)。在signal()方法被调用后，系统会从Condition对象的等待队列中唤醒一个
         * 线程，一旦线程被唤醒，被唤醒的线程就尝试重新获取锁，一旦获取成功，就可以继续执行了。因此，在signal被调用后，
         * 一般需要释放相关的锁，让给其他被唤醒的线程，让他可以继续执行
         *
         *
         */


        /**
         * Condition接口提供的常用方法：
         * 1、和Object中wait类似的方法
         * void await() throws InterruptedException:当前线程进入等待状态，如果其他线程调用condition的signal或者signalAll方法并且当前线程获取Lock从await方法返回，如果在等待状态中被中断会抛出被中断异常；
         * long awaitNanos(long nanosTimeout)：当前线程进入等待状态直到被通知，中断或者超时；
         * boolean await(long time, TimeUnit unit) throws InterruptedException：同第二种，支持自定义时间单位，false：表示方法超时之后自动返回的，true：表示等待还未超时时，await方法就返回了（超时之前，被其他线程唤醒了）
         * boolean awaitUntil(Date deadline) throws InterruptedException：当前线程进入等待状态直到被通知，中断或者到了某个时间
         * void awaitUninterruptibly();：当前线程进入等待状态，不会响应线程中断操作，只能通过唤醒的方式让线程继续
         * 2、和Object中notify/notifyAll类似的方法
         * void signal();唤醒一个等待在Condition上的线程，将线程从等待队列中转移到同步队列中，如果在同步队列中能够竞争到Lock则可以从等待方法[await所在的方法，自己写的]中返回
         * void signalAll();能够唤醒所有等待在Condition上的线程
         *
         */
    }

}