package com.juc.chat03;

/**
 * 程序只有守护线程时，系统会自动退出
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/29
 */
public class Demo2 {

    static class T1 extends Thread {

        public T1(String name) {
            super(name);
        }
        @Override
        public void run() {
            System.out.println(this.getName() + "开始执行，" + (this.isDaemon() ? "我是守护线程" : "我是用户线程"));
            while (true) {
                ;
            }
        }
    }

    public static void main(String[] args) {
        T1 t1 = new T1("子线程1");
        t1.setDaemon(true);
        t1.start();
        System.out.println("主线程结束");
    }
}