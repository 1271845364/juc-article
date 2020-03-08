package com.juc.chat02;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/29
 */
public class Demo3 {

    /**
     * 主线程的线程组为main
     * 根线程组为system
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(Thread.currentThread());
        System.out.println(Thread.currentThread().getThreadGroup());
        System.out.println(Thread.currentThread().getThreadGroup().getParent());
        System.out.println(Thread.currentThread().getThreadGroup().getParent().getParent());
    }
}