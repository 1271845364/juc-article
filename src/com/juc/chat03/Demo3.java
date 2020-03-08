package com.juc.chat03;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/30
 */
public class Demo3 {

    static class T1 extends Thread {
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置为守护线程，必须在start()方法执行之前
     *
     * @param args
     */
    public static void main(String[] args) {
        T1 t1 = new T1();
        t1.start();
        t1.setDaemon(true);
    }
}