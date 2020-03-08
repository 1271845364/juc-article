package com.juc.chat04;

/**
 * 分析代码是否互斥的方法，先找出synchronized作用的对象是谁，如果多个线程操作的方法中synchronized作用的锁对象一样，那么这些线程同时异步执行这些方法就是互斥的
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/03
 */
public class Demo6 {

    /**
     * 作用于当前类的实例对象
     */
    public synchronized void m1() {
    }

    /**
     * 作用于当前类的实例对象
     */
    public synchronized void m2() {
    }

    /**
     * 作用于当前类的实例对象
     */
    public void m3() {
        synchronized (this) {

        }
    }

    /**
     * 作用于当前类Class对象
     */
    public synchronized static void m4() {
    }

    /**
     * 作用于当前Class对象
     */
    public static void m5() {
        synchronized (Demo6.class){
        }
    }

    public static class T extends Thread {

        Demo6 demo6;

        public T(Demo6 demo6){
            this.demo6 = demo6;
        }


        @Override
        public void run() {
            super.run();
        }
    }

    /**
     * 1、线程t1、t2、t3中调用的方法是互斥的，都需要获取d1这把锁
     * 2、线程t1/t2/t3和线程t4是不互斥的，可以同时运行，前面三个线程依赖d1这把锁，而t4以依赖的是d2的锁
     * 3、线程t5/t6都作用于当前类的Class对象锁，所以这两个线程是互斥的额，和其他几个线程是不互斥的
     *
     * @param args
     */
    public static void main(String[] args) {
        Demo6 d1 = new Demo6();
        Thread t1 = new Thread(()->{
            d1.m1();
        });
        t1.start();

        Thread t2 = new Thread(()->{
            d1.m2();
        });
        t2.start();

        Thread t3 = new Thread(()->{
            d1.m3();
        });
        t3.start();

        Demo6 d2 = new Demo6();
        Thread t4 = new Thread(()->{
            d2.m2();
        });
        t4.start();

        Thread t5 = new Thread(()->{
            Demo6.m4();
        });
        t2.start();

        Thread t6 = new Thread(()->{
            Demo6.m5();
        });
        t6.start();
    }

}