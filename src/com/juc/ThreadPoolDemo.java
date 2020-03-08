package com.juc;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 当自定义线程池的时候，设置的corePoolSize数量小于maximumPoolSize，并且使用的是无界队列（这里的无界队列指的是LinkedBlockingQueue）的时候，
 * 当线程池线程数量达到corePoolSize，这个时候继续往线程池里提交任务，线程池会怎么处理
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2020/01/09
 */
public class ThreadPoolDemo {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 1,
                TimeUnit.MINUTES, new LinkedBlockingQueue<>(1));

        for (int i = 1; i <= 7; i++) {
            threadPoolExecutor.execute(new Task(i, threadPoolExecutor));
            Thread.sleep(10);
        }
    }

    static class Task implements Runnable {
        private int i;
        private ThreadPoolExecutor threadPoolExecutor;

        public Task(int i, ThreadPoolExecutor threadPoolExecutor) {
            this.i = i;
            this.threadPoolExecutor = threadPoolExecutor;
        }

        @Override
        public void run() {
            System.out.println("线程:" + i + "运行,poolSize=" + threadPoolExecutor.getPoolSize());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}