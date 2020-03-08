package com.juc.chat12;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/16
 */
public class Demo1 {

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(System.currentTimeMillis() + "," + thread.getName() + ",释放许可!");
                semaphore.release();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new T("t-" + i).start();
        }

        /**
         * 输出结果：
         * 1568634877216,t-0,获取许可!
         * 1568634877217,t-1,获取许可!
         * 1568634880218,t-0,释放许可!
         * 1568634880218,t-1,释放许可!
         * 1568634880218,t-2,获取许可!
         * 1568634880218,t-3,获取许可!
         * 1568634883219,t-2,释放许可!
         * 1568634883219,t-3,释放许可!
         * 1568634883219,t-4,获取许可!
         * 1568634883219,t-5,获取许可!
         * 1568634886220,t-5,释放许可!
         * 1568634886220,t-4,释放许可!
         * 1568634886220,t-6,获取许可!
         * 1568634886220,t-7,获取许可!
         * 1568634889220,t-6,释放许可!
         * 1568634889220,t-7,释放许可!
         * 1568634889220,t-8,获取许可!
         * 1568634889220,t-9,获取许可!
         * 1568634892221,t-8,释放许可!
         * 1568634892221,t-9,释放许可!
         *
         * new Semaphore(2)创建了许可数量为2的信号量，每个线程获取1个许可，同时允许两个线程获取许可，从输出中也可以看出，同时有两个
         * 线程可以获取许可，其他线程需要等待以获取许可的线程释放许可之后才能运行。为获取到许可的线程会阻塞在acquire()方法上，直到获取到
         * 许可才能继续
         *
         */
    }

}