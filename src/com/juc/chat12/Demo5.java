package com.juc.chat12;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 在规定的时间内获取许可
 * <p>
 * 超时获取许可的功能
 * public boolean tryAcquire(long timeout, TimeUnit unit)
 * throws InterruptedException {
 * return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
 * }
 * <p>
 * public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
 * throws InterruptedException {
 * if (permits < 0) throw new IllegalArgumentException();
 * return sync.tryAcquireSharedNanos(permits, unit.toNanos(timeout));
 * }
 * <p>
 * 在指定的时间内获取许可，如果能够获取到返回true，获取不到返回false
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/16
 */
public class Demo5 {

    static Semaphore semaphore = new Semaphore(1);

    public static class T extends Thread {
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            //获取许可是否成功
            boolean acquireSuccess = false;
            try {
                //尝试在1s内获取许可，获取成功返回true，否则返回false
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",尝试获取许可，当前许可数量：" + semaphore.availablePermits());
                acquireSuccess = semaphore.tryAcquire(1, TimeUnit.SECONDS);
                if (acquireSuccess) {
                    System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",获取许可成功，当前许可数量：" + semaphore.availablePermits());
                    //休眠5s
                    TimeUnit.SECONDS.sleep(5);
                } else {
                    System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",获取许可失败，当前许可数量：" + semaphore.availablePermits());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (acquireSuccess) {
                    semaphore.release();
                }
            }
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

        /**
         * 输出结果：
         * 1568640405693,t1,尝试获取许可，当前许可数量：1
         * 1568640405694,t1,获取许可成功，当前许可数量：0
         * 1568640406694,t2,尝试获取许可，当前许可数量：0
         * 1568640407694,t3,尝试获取许可，当前许可数量：0
         * 1568640407695,t2,获取许可失败，当前许可数量：0
         * 1568640408695,t3,获取许可失败，当前许可数量：0
         *
         * 许可数量为1，semaphore.tryAcquire(1,TimeUnit.SECONDS);表示尝试在1s内获取许可，
         * 获取成功立即返回true，超过1s还是获取不到，返回false。线程t1获取许可成功之后，之后休眠5s
         * ，从输出结果看出，t2和t3都尝试了1s，获取失败了
         *
         *
         * 总结：
         * 1、semaphore默认创建的是非公平的信号量，非公平的效率更高
         * 2、方法中带有throws InterruptedException声明的，表示这个方法会响应线程中断信号：表示调用线程的interrupt()方法后，
         * 会让这些方法触发InterruptedException异常，即使这些方法处于阻塞状态，也会立即返回，并抛出InterruptedException异常，
         * 线程中断信号也会被清除
         *
         */

    }
}