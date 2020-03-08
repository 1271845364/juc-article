package com.juc.chat12;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/16
 */
public class Demo3 {

    static Semaphore semaphore = new Semaphore(1);

    public static class T extends Thread {
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            try {
                semaphore.acquire();
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",获取许可，当前许可数量：" + semaphore.availablePermits());
                //休眠100s
                TimeUnit.SECONDS.sleep(100);
                System.out.println(System.currentTimeMillis() +  "," + thread.getName() + ",运行结束!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
            System.out.println(System.currentTimeMillis() + "," + thread.getName() + "，当前可用许可数量：" + semaphore.availablePermits());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T t1 = new T("t1");
        t1.start();

        //休眠1s
        TimeUnit.SECONDS.sleep(1);
        T t2 = new T("t2");
        t2.start();

        //休眠1s
        TimeUnit.SECONDS.sleep(1);
        T t3 = new T("t3");
        t3.start();

        //给t2和t3发送中断信号
        t2.interrupt();
        t3.interrupt();

        /**
         * 输出结果：
         * 1568638774382,t1,获取许可，当前许可数量：0
         * java.lang.InterruptedException
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireSharedInterruptibly(AbstractQueuedSynchronizer.java:1302)
         * 	at java.util.concurrent.Semaphore.acquire(Semaphore.java:312)
         * 	at com.juc.chat12.Demo1$T.run(Demo1.java:23)
         * java.lang.InterruptedException
         * 1568638776385,t3，当前可用许可数量：1
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.doAcquireSharedInterruptibly(AbstractQueuedSynchronizer.java:998)
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireSharedInterruptibly(AbstractQueuedSynchronizer.java:1304)
         * 	at java.util.concurrent.Semaphore.acquire(Semaphore.java:312)
         * 	at com.juc.chat12.Demo1$T.run(Demo1.java:23)
         * 1568638776386,t2，当前可用许可数量：2
         *
         * 程序中许可数量为1，创建了3个线程获取许可，线程t1获取成功了，然后休眠100s。其他
         * 两个线程阻塞在semaphore.acqurire();方法处，代码中对线程t2、t3发送中断信号，
         * 我们看一下Semaphore中的acquire的源码：
         *  public void acquire() throws InterruptedException {
         *       sync.acquireSharedInterruptibly(1);
         *  }
         * 这个方法会响应线程中断，主线程中对t2、t3发送中断信号后，acquire()方法会触发InterruptedException异常，
         * t2、t3最终没有获取到许可，但是他们都执行了finally中的释放许可的操作，最后导致许可数量变为了2，导致许可数量增加了。
         * 所以程序中释放许可的方式有问题。改进：只有获取到许可才可以释放许可
         *
         *
         */
    }
}