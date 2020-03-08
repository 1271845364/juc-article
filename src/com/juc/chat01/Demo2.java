package com.juc.chat01;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo2 {

    /**
     * interrupt()方法被调用之后，线程的中断标志将被置为true，循环体中通过检查线程的中断标志是否为ture（ this.isInterrupted()）来判断线程是否需要退出了。
     *
     * public void interrupt()中断线程
     *
     * public boolean isInterrupted()判断线程是否被中断
     *
     * public static boolean interrupted()判断线程是否被中断，并清除当前中断状态
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (this.isInterrupted()) {
                        System.out.println("我要退出了!");
                        break;
                    }
                }
            }
        };
        thread.setName("thread1");
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        thread.interrupt();



    }

}