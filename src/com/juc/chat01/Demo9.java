package com.juc.chat01;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/29
 */
public class Demo9 {

    public static volatile boolean flag = true;

    static class T1 extends Thread {
        @Override
        public void run() {
            System.out.println("线程" + this.getName() + " in");
            while (flag) {
                ;
            }
            System.out.println("线程" + this.getName() + "停止了");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T1 t1 = new T1();
        t1.setName("t1");
        t1.start();
        Thread.sleep(1000);
        flag = false;
    }

}