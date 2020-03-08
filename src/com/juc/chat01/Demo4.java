package com.juc.chat01;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo4 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        //调用interrupt()方法之后，线程的sleep方法将会抛出 InterruptedException异常。运行上面的代码，发现程序无法终止。为什么？
        //sleep方法由于中断而抛出异常之后，线程的中断标志会被清除（置为false），所以在异常中需要执行this.interrupt()方法，将中断标志位置为true

    }
}