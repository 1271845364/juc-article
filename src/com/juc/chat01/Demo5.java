package com.juc.chat01;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo5 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        this.interrupt();
                        e.printStackTrace();
                    }
                    if(this.isInterrupted()) {
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