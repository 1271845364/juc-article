package com.juc.chat05;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo2 {

    public static class T extends Thread {
        @Override
        public void run() {
            while (true) {
                if (this.isInterrupted()) {
                    break;
                }
            }
        }
    }

    /**
     * 运行程序，程序可以正常结束。线程内部有个中断标志，当调用线程的interrupt()实例方法之后，
     * 线程的中断标志会被置为true，可以通过线程的实例方法isInterrupted()获取线程的中断标志。
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