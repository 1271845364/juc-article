package com.juc.chat01;

/**
 * Thread suspend() resume()
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo7 {

    static Object object = new Object();

    /**
     * 发现t2线程在suspend0处被挂起了，t2的状态竟然还是RUNNABLE状态，线程明明被挂起了，状态还是运行中容易导致我们对当前系统进行误判，
     * 代码中已经调用resume()方法了，但是由于时间先后顺序的缘故，resume并没有生效，这导致了t2永远的被挂起了，
     * 并且永远占用了object的锁，这对于系统来说可能是致命的。
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        T t1 = new T("t1");
        t1.start();
        Thread.sleep(100);

        T t2 = new T("t2");
        t2.start();

        t1.resume();
        t2.resume();
        System.out.println("t2.resume...");

        t1.join();
        t2.join();
    }

    static class T extends Thread {
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (object) {
                System.out.println("in " + this.getName());
                this.suspend();
            }
        }
    }

}