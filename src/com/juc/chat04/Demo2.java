package com.juc.chat04;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo2 {

    int num = 0;

    public synchronized void add() {
        num++;
    }

    static class T extends Thread {

        private Demo2 demo2;

        public T(Demo2 demo2) {
            this.demo2 = demo2;
        }

        //没有获取到锁的线程将等待，直到其他线程释放锁为止。
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                this.demo2.add();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Demo2 demo2 = new Demo2();
        T t1 = new T(demo2);
        t1.start();
        T t2 = new T(demo2);
        t2.start();

        //等待t1和t2执行结束
        t1.join();
        t2.join();
        System.out.println(demo2.num);
    }


}