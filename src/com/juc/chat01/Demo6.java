package com.juc.chat01;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo6 {

    static Object object = new Object();

    /**
     * 注意下打印结果，T2调用notify方法之后，T1并不能立即继续执行，而是要等待T2释放objec投递锁之后，T1重新成功获取锁后，才能继续执行。
     * 因此最后2行日志相差了2秒（因为T2调用notify方法后休眠了2秒）。
     *
     * @param args
     */
    public static void main(String[] args) {
        new T1().start();
        new T2().start();
    }

    static class T1 extends Thread {
        @Override
        public void run() {
            synchronized (object) {
                System.out.println("t1 start");
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1 end");
            }
        }
    }

    static class T2 extends Thread {
        @Override
        public void run() {
            synchronized (object) {
                System.out.println("t2 start");
                object.notify();
                System.out.println("t2 end");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}