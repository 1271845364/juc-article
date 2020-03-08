package com.juc.chat06;

/**
 * synchronized实现共享变量++操作
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/04
 */
public class Demo2 {

    private static int num = 0;

    public static class T extends Thread {
        @Override
        public void run() {
            Demo2.add();
        }
    }

    private synchronized static void add() {
        for (int i = 0; i < 1000; i++) {
            num++;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        T t1 = new T();
        t1.start();
        T t2 = new T();
        t2.start();
        T t3 = new T();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(num);
    }
}