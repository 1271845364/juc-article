package com.juc.chat03;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/30
 */
public class Demo4 {

    static class T1 extends Thread {

        public T1(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(this.getName() + ".daemon:" + this.isDaemon());
        }
    }

    /**
     * Thread中init()方法中
     * Thread parent = currentThread();
     * this.daemon = parent.isDaemon();
     * dameon的默认值为父线程的daemon，也就是说，父线程如果为用户线程，子线程默认也是用户线程，父线程如果是守护线程，子线程默认也是守护线程。
     *
     * t1是由主线程(main方法所在的线程)创建的，main线程是t1的父线程，所以t1.daemon为false，说明t1是用户线程。
     * t2线程调用了 setDaemon(true);将其设为守护线程，t3是由t2创建的，所以t3默认线程类型和t2一样，t2.daemon为true。
     *
     * 线程的daemon默认值和其父线程一样
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ".daemon:" + Thread.currentThread().isDaemon());
        T1 t1 = new T1("t1");
        t1.start();

        Thread t2 = new Thread(){
            @Override
            public void run() {
                System.out.println(this.getName() + ".daemon:" + this.isDaemon());
                T1 t3 = new T1("t3");
                t3.start();
            }
        };

        t2.setName("t2");
        t2.setDaemon(true);
        t2.start();

        TimeUnit.SECONDS.sleep(2);
    }
}