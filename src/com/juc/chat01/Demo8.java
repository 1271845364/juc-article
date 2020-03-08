package com.juc.chat01;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo8 {

    static int num = 0;

    public static void main(String[] args) throws InterruptedException {
        T t1 = new T("t1");
        t1.start();

        t1.join();//等待t1线程结束，在往下面执行
        System.out.println(System.currentTimeMillis() + ",num=" + num);
    }

    static class T extends Thread {
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ",start " + this.getName());
            for (int i = 0; i < 10; i++) {
                num++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(System.currentTimeMillis() + ",end " + this.getName());
        }
    }
}