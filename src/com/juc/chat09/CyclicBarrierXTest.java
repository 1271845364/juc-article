package com.juc.chat09;

import java.util.concurrent.TimeUnit;

/**
 * wait/notify/notifyAll方法使用的问题和解决方案
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/06
 */
public class CyclicBarrierXTest {

    /**
     * 线程1先运行获得sum对象锁（通过synchronized），但是随后执行了sum.wait()方法，主动释放掉了sum对象锁，
     * 然后线程2获得了sum对象锁（通过synchronized）,也通过sum.wait()失去sum的对象锁，最后线程3获得了sum对象锁（通过synchronized），
     * 主动通过sum.notify()通知了线程1或者2，假设是1，线程1重新通过notify()/notifyAll()的方式获得了锁，
     * 然后执行完毕，随后线程释放锁，然后这个时候线程2成功获得锁，执行完毕。
     *
     * @param args
     */
    public static void main(String[] args) {
        final SumX sum = new SumX();

        new Thread(() -> {
            synchronized (sum) {
                System.out.println("thread1 get lock");
                try {
                    sum.wait();//主动释放掉sum对象锁
                    System.out.println(sum.total);
                    System.out.println("thread1 release lock");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (sum) {
                System.out.println("thread2 get lock");
                try {
                    sum.wait();//释放掉sum对象锁，等待其他线程的唤醒(其他对象释放sum锁)
                    System.out.println(sum.total);
                    System.out.println("thread2 release lock");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (sum) {
                System.out.println("thread3 get lock");
                try {
                    sum.sum();
                    //此时唤醒没用，因为没有线程等待
                    sum.notifyAll();
                    TimeUnit.MILLISECONDS.sleep(2000);
                    System.out.println("thread3 really release lock");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /**
         * 运行结果：
         * thread1 get lock
         * thread2 get lock
         * thread3 get lock
         * thread3 really release lock
         * 100
         * thread2 release lock
         * 100
         * thread1 release lock
         *
         */
    }
}

class SumX {
    public Integer total = 0;

    public void sum() throws InterruptedException {
        total = 100;
        TimeUnit.MILLISECONDS.sleep(500);
    }
}