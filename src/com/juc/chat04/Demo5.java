package com.juc.chat04;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo5 implements Runnable {

    static Demo5 instance = new Demo5();

    static int i = 0;

    @Override
    public void run() {
        synchronized (instance) {
            for (int j = 0; j < 1000; j++) {
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(instance);
        t.start();
        Thread t2 = new Thread(instance);
        t2.start();

        t.join();
        t2.join();

        System.out.println(i);
    }
}