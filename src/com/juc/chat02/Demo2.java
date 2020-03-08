package com.juc.chat02;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/29
 */
public class Demo2 {

    static class R1 implements Runnable {
        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            System.out.println("所属线程组：" + thread.getThreadGroup().getName() + ",线程名字：" + thread.getName());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * threadGroup1未指定父线程组，系统获取了主线程的线程组作为threadGroup1的父线程组，输出结果中是：main
     * threadGroup1为threadGroup2的父线程组
     * threadGroup1活动线程数为4，包含了threadGroup1线程组中的t1、t2，以及子线程组threadGroup2中的t3、t4
     * 线程组的list()方法，将线程组中的所有子孙节点信息输出到控制台，用于调试使用
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup threadGroup1 = new ThreadGroup("thread-group-1");
        Thread t1 = new Thread(threadGroup1,new R1(),"t1");
        Thread t2 = new Thread(threadGroup1,new R1(),"t2");
        t1.start();
        t2.start();

        TimeUnit.SECONDS.sleep(1);
        System.out.println("threadGroup1活动线程数：" + threadGroup1.activeCount());
        System.out.println("threadGroup1活动线程组：" + threadGroup1.activeGroupCount());
        System.out.println("threadGroup1活动线程组名称：" + threadGroup1.getName());
        System.out.println("threadGroup1父线程组名称：" + threadGroup1.getParent().getName());
        System.out.println("----------------------------");

        ThreadGroup threadGroup2 = new ThreadGroup(threadGroup1,"thread-group-2");
        Thread t3 = new Thread(threadGroup2,new R1(),"t3");
        Thread t4 = new Thread(threadGroup2,new R1(),"t4");
        t3.start();
        t4.start();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("threadGroup2活动线程数：" + threadGroup2.activeCount());
        System.out.println("threadGroup2活动线程组：" + threadGroup2.activeGroupCount());
        System.out.println("threadGroup2活动线程组名称：" + threadGroup2.getName());
        System.out.println("threadGroup2父线程组名称：" + threadGroup2.getParent().getName());
        System.out.println("----------------------------");

        System.out.println("threadGroup1活动线程数：" + threadGroup1.activeCount());
        System.out.println("threadGroup1活动线程组：" + threadGroup1.activeGroupCount());
        System.out.println("----------------------------");

        threadGroup1.list();
    }
}