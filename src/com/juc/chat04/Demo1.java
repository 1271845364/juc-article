package com.juc.chat04;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/30
 */
public class Demo1 {

    static int num = 0;

    static class T1 extends Thread {
        @Override
        public void run() {
            Demo1.m1();
        }
    }

    private synchronized static void m1() {
        for (int i = 0; i < 10000; i++) {
            num++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T1 t1 = new T1();
        T1 t2 = new T1();
        T1 t3 = new T1();
        t1.start();
        t2.start();
        t3.start();

        //等待三个线程打印num
        t1.join();
        t2.join();
        t3.join();

        System.out.println(Demo1.num);//23585
    }
}