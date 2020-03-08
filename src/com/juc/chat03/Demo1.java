package com.juc.chat03;

/**
 * 程序只有守护线程时，系统会自动退出
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/29
 */
public class Demo1 {

    static class T1 extends Thread {
        @Override
        public void run() {
            System.out.println(this.getName() + "开始执行，" + (this.isDaemon() ? "我是守护线程" : "我是用户线程"));
            while (true) {
                ;
            }
        }
    }

    public static void main(String[] args) {
        T1 t1 = new T1();
        t1.setName("子线程1");
        t1.start();
        System.out.println("子线程结束");
    }
}