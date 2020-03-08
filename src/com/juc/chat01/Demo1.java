package com.juc.chat01;

import java.util.concurrent.TimeUnit;

/**
 * Thread stop() 立刻将一个线程终止
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo1 {

    /**
     * 代码中有个死循环，调用stop方法之后，线程thread1的状态变为TERMINATED（结束状态），线程停止了。
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("start");
                boolean flag = true;
                while (flag) {
                    ;
                }
                System.out.println("end");
            }
        };
        thread1.setName("thread1");
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        thread1.stop();

        System.out.println(thread1.getState());
        TimeUnit.SECONDS.sleep(1);
        System.out.println(thread1.getState());
    }
}