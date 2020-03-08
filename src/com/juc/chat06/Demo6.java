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
public class Demo6 {

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
        //先运行一下上面代码，发现程序无法结束，使用jstack查看线程堆栈信息，发现2个线程死锁了。
    }
}