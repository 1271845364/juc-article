package com.juc.chat05;

import java.util.concurrent.TimeUnit;

/**
 * 通过一个变量控制线程中断
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo1 {

    public volatile static boolean exit = false;

    public static class T extends Thread {
        @Override
        public void run() {
            while(true) {
                if (exit) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T t = new T();
        t.start();
        TimeUnit.SECONDS.sleep(1);
        exit = true;
    }
}