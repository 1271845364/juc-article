package com.juc.chat12;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/16
 */
public class Demo4 {

    static Semaphore semaphore = new Semaphore(1);

    public static class T extends Thread {
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            boolean acquireSuccess = false;
            try {
                semaphore.acquire();
                acquireSuccess = true;
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",获取许可，当前许可数量：" + semaphore.availablePermits());
                //休眠100s
                TimeUnit.SECONDS.sleep(100);
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",运行结束!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (acquireSuccess) {
                    semaphore.release();
                }
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
         * 1568639630231,t1,获取许可，当前许可数量：0
         * java.lang.InterruptedException
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.doAcquireSharedInterruptibly(AbstractQueuedSynchronizer.java:998)
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireSharedInterruptibly(AbstractQueuedSynchronizer.java:1304)
         * 	at java.util.concurrent.Semaphore.acquire(Semaphore.java:312)
         * 	at com.juc.chat12.Demo4$T.run(Demo4.java:24)
         * java.lang.InterruptedException1568639632231,t2，当前可用许可数量：0
         *
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireSharedInterruptibly(AbstractQueuedSynchronizer.java:1302)
         * 	at java.util.concurrent.Semaphore.acquire(Semaphore.java:312)
         * 	at com.juc.chat12.Demo4$T.run(Demo4.java:24)
         * 1568639632231,t3，当前可用许可数量：0
         * 1568639730232,t1,运行结束!
         * 1568639730232,t1，当前可用许可数量：1
         *
         * 程序中增加了一个变量acquireSuccess用来标记获取许可是否成功，在finally中根据这个变量是否为true，来确定是否释放许可
         *
         *
         *
         *
         */
    }
}