package com.juc.chat02;

import java.util.concurrent.TimeUnit;

/**
 * 批量中断线程
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/29
 */
public class Demo4 {

    static class R1 implements Runnable {
        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            System.out.println("所属线程组：" + thread.getThreadGroup().getName() + ",线程名称：" + thread.getName());
            while (!thread.isInterrupted()) {
                ;
            }
            System.out.println("线程：" + thread.getName() + "停止了!");
        }
    }

    /**
     * 停止线程之后，通过list()方法可以看出输出的信息中不包含已结束的线程了。
     * 创建线程或者线程组的时候，给他们取一个有意义的名字，
     * 对于计算机来说，可能名字并不重要，但是在系统出问题的时候，你可能会去查看线程堆栈信息，
     * 如果你看到的都是t1、t2、t3，估计自己也比较崩溃，如果看到的是httpAccpHandler、dubboHandler类似的名字，应该会好很多。
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup threadGroup1 = new ThreadGroup("thread-group-1");
        Thread t1 = new Thread(threadGroup1, new R1(), "t1");
        Thread t2 = new Thread(threadGroup1, new R1(), "t2");
        t1.start();
        t2.start();

        ThreadGroup threadGroup2 = new ThreadGroup(threadGroup1, "thread-group-2");
        Thread t3 = new Thread(threadGroup2, new R1(), "t3");
        Thread t4 = new Thread(threadGroup2, new R1(), "t4");
        t3.start();
        t4.start();
        TimeUnit.SECONDS.sleep(1);

        System.out.println("---------threadGroup1信息--------");
        threadGroup1.list();

        System.out.println("---------------------------------");
        System.out.println("停止线程组：" + threadGroup1.getName() + "中的所有子线程");
        threadGroup1.interrupt();
        TimeUnit.SECONDS.sleep(2);

        System.out.println("---------threadGroup1停止后，输出信息--------");
        threadGroup1.list();
    }
}