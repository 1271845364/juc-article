package com.juc.chat12;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/16
 */
public class Demo2 {

    static Semaphore semaphore = new Semaphore(2);

    public static class T extends Thread {
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            try {
                semaphore.acquire();
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",获取许可!");
                TimeUnit.SECONDS.sleep(3);
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",运行结束!");
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",当前可用许可数量：" + semaphore.availablePermits());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new T("t-" + i).start();
        }

        /**
         *
         * 获取许可之后不释放
         *
         * 输出结果：
         * 1568635383437,t-0,获取许可!
         * 1568635383438,t-1,获取许可!
         * 1568635386438,t-1,运行结束!
         * 1568635386438,t-0,运行结束!
         * 1568635386438,t-1,当前可用许可数量：0
         * 1568635386438,t-0,当前可用许可数量：0
         *
         * 程序无法结束，获取许可之后，没有释放许可的代码，最终导致，可用许可数量为0，其他线程无法获取许可，会在semaphore.acquire();
         * 处等待，导致程序无法结束
         *
         */
    }

}