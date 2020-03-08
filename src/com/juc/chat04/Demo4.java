package com.juc.chat04;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo4 {

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

    static int num;

    static class T extends Thread {
        @Override
        public void run() {
            add();
        }
    }

    public static void add() {
        //同步代码块
        synchronized (Demo4.class) {
            for (int i = 0; i < 1000; i++) {
                num++;
            }
        }
    }
}