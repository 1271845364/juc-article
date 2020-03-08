package com.juc.chat05;

import java.util.concurrent.TimeUnit;

/**
 * 线程中断
 *
 * 当一个线程处于被阻塞状态或者试图执行一个阻塞操作时，可以使用 Thread.interrupt()方式中断该线程，
 * 注意此时将会抛出一个InterruptedException的异常，同时中断状态将会被复位(由中断状态改为非中断状态)
 * 内部有循环体，可以通过一个变量来作为一个信号控制线程是否中断，注意变量需要volatile修饰
 * 文中的几种方式可以结合起来灵活使用控制线程的中断
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo3 {

    public static class T extends Thread {
        @Override
        public void run() {
            //阻塞状态的线程如何中断
            while (true) {
                try {
                    //循环处理业务
                    //下面模拟阻塞代码
                    TimeUnit.SECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    this.interrupt();
                }
                if (this.isInterrupted()) {
                    break;
                }
            }
        }
    }

    /**
     * 1、调用线程的interrupt()方法，线程中断标志会被置为true
     * 2、当线程处于阻塞状态时，调用线程的interrupt()方法，线程内部会触发InterruptedException异常，并且会清除线程内部的中断标志(即将标志位置为false)
     * <p>
     * java.lang.InterruptedException: sleep interrupted
     * at java.lang.Thread.sleep(Native Method)
     * at java.lang.Thread.sleep(Thread.java:340)
     * at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
     * at com.juc.chat05.Demo1$T.run(Demo1.java:19)
     * <p>
     * <p>
     * main方法中调用了t.interrupt()方法，此时线程t内部的中断标志会置为true
     * <p>
     * 然后会触发run()方法内部的InterruptedException异常，所以运行结果中有异常输出，上面说了，当触发InterruptedException异常时候，
     * 线程内部的中断标志又会被清除（变为false），所以在catch中又调用了this.interrupt();一次，将中断标志置为true
     * <p>
     * run()方法中通过this.isInterrupted()来获取线程的中断标志，退出循环（break）
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        T t = new T();
        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
    }

}