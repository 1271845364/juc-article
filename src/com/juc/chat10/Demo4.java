package com.juc.chat10;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/11
 */
public class Demo4 {

    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try{
                    System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
                }finally {
                    lock.unlock();
                }
            }
        });

        thread.setName("t1");
        thread.start();
        //休眠5s
        TimeUnit.SECONDS.sleep(5);
        lock.lock();
        try {
            condition.signal();
        }finally {
            lock.unlock();
        }

        /**
         *
         * 输出结果：
         * 1568207140094:t1 start
         * 1568207145094:t1 被唤醒
         *
         * t1线程启动之后调用condition.await()方法将线程处于等待中，主线程休眠5s之后调用condition.signal()方法将线程t1唤醒成功，结果相差5s
         *
         */
    }
}