package com.juc.chat06;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 关于获取锁的过程中被中断，注意几点:
 * 1、ReentrankLock中必须使用实例方法 lockInterruptibly()获取锁时，在线程调用interrupt()方法之后，才会引发 InterruptedException异常
 * 2、线程调用interrupt()之后，线程的中断标志会被置为true
 * 3、触发InterruptedException异常之后，线程的中断标志有会被清空，即置为false
 * 4、所以当线程调用interrupt()引发InterruptedException异常，中断标志的变化是:false->true->false
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/04
 */
public class Demo7 {

    private static ReentrantLock lock1 = new ReentrantLock();
    private static ReentrantLock lock2 = new ReentrantLock();

    public static class T extends Thread {
        int lock;

        public T(String name, int lock) {
            super(name);
            this.lock = lock;
        }

        /**
         * lock1被线程t1占用，lock2被线程t2占用，线程t1在等待获取lock2，线程t2在等待获取lock1，
         * 都在相互等待获取对方持有的锁，最终产生了死锁，如果是在synchronized关键字情况下发生了死锁现象，程序是无法结束的。
         */
        @Override
        public void run() {
            try {
                if (this.lock == 1) {
                    lock1.lockInterruptibly();
                    TimeUnit.SECONDS.sleep(1);
                    lock2.lockInterruptibly();
                    System.out.println("this.lock=1");
                } else {
                    lock2.lockInterruptibly();
                    TimeUnit.SECONDS.sleep(1);
                    lock1.lockInterruptibly();
                }
            } catch (InterruptedException e) {
                System.out.println(this.getName() + "中断标志：" + this.isInterrupted());
                e.printStackTrace();
            } finally {
                //lock1这个锁是否被当前线程锁持有，如果是，就释放锁
                if (lock1.isHeldByCurrentThread()) {
                    System.out.println(this.getName() + ":unlock1");
                    lock1.unlock();
                }
                if (lock2.isHeldByCurrentThread()) {
                    System.out.println(this.getName() + ":unlock2");
                    lock2.unlock();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T t1 = new T("t1", 1);
        T t2 = new T("t2", 2);

        t1.start();
        t2.start();

        //我们对上面代码改造一下，线程t2一直无法获取到lock1，那么等待5秒之后，我们中断获取锁的操作
        TimeUnit.SECONDS.sleep(5);
        t2.interrupt();
        /**
         * t2在程序的44行获取不到lock1的锁，主线程中等待了5s,将t2的中断标志设置为true，44行触发了，然后t2继续往后执行代码，释放了lock2
         * 然后t1就可以正常获取锁，程序继续执行；t2发送中断信号触发InterruptedException异常之后，中断标志将被清空
         */
    }
}