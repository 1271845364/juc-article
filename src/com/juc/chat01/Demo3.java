package com.juc.chat01;

import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/28
 */
public class Demo3 {

    /**
     * 代码中通过一个变量isStop来控制线程是否停止。
     * <p>
     * 通过变量控制和线程自带的interrupt方法来中断线程有什么区别呢？
     * <p>
     * 如果一个线程调用了sleep方法，一直处于休眠状态，通过变量控制，还可以中断线程么？大家可以思考一下。
     * <p>
     * 此时只能使用线程提供的interrupt方法来中断线程了。
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (isStop) {
                        System.out.println("我要退出了!");
                        break;
                    }
                }
            }
        };
        thread.setName("thread1");
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        isStop = true;
    }

    static volatile boolean isStop = false;
}