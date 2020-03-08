package com.juc.chat04;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo3 {

    static int num = 0;

    public static synchronized void add() {
        num++;
    }

    static class T extends Thread {

        //没有获取到锁的线程将等待，直到其他线程释放锁为止。
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                add();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T t1 = new T();
        t1.start();
        T t2 = new T();
        t2.start();
        T t3 = new T();
        t3.start();

        //等待t1和t2执行结束
        t1.join();
        t2.join();
        t3.join();
        System.out.println(num);
    }


}