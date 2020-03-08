package com.juc.chat09;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition.await过程中被打断
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/10
 */
public class Demo4 {

    static Lock lock = new ReentrantLock();

    static Condition condition = lock.newCondition();

    private static class T1 extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                System.out.println("中断标志：" + this.isInterrupted());
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T1 t1 = new T1();
        t1.setName("t1");
        t1.start();

        TimeUnit.SECONDS.sleep(2);
        System.out.println("1、t1中断标志：" + t1.isInterrupted());
        t1.interrupt();
        System.out.println("2、t1中断标志：" + t1.isInterrupted());

        /**
         * 调用condition.await()之后，线程t1进入阻塞状态，调用t1.interrupt()，会给线程t1发送中断信号，await()
         * 方法内部会检测线程中断信号，然后出发InterruptedException异常，线程中断标志被清除。从输出的结果中可以看出，
         * 线程t1中断标志的变换过程：false -> true -> false
         */
    }
}