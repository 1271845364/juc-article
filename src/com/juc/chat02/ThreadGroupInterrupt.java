package com.juc.chat02;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程组中断
 *
 * 用户表按用户ID分成了10个表，这时候想通过用户昵称找到这个用户，则这时候就可以使用threadGroup中的 interrupt 来实现了
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/23
 */
public class ThreadGroupInterrupt {

    public static void main(String[] args) {
        int threadNum = 10;
        final ThreadGroup threadGroup = new ThreadGroup("search-threadgroup");
        List<Thread> list = new ArrayList<>();
        //定义10个线程
        for (int i = 0; i < threadNum; i++) {
            Thread t = new Thread(threadGroup, new SearchRunnable(5000 + i * 1000), "search-thread-" + i);
            list.add(t);
            t.start();
            System.out.println("start thread = " + t);
        }

        //监控线程活动的子线程数
        new Thread(() -> {
            int activeCount = threadGroup.activeCount();
            while (activeCount > 0) {
                System.out.println("activeCount=" + activeCount);
                if (activeCount < 5) {
                    System.out.println("找到了需要的文件，开始终止其他的子线程");
                    threadGroup.interrupt();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activeCount = threadGroup.activeCount();
            }
        }).start();

        /**
         * 输出结果：
         * start thread = Thread[search-thread-0,5,search-threadgroup]
         * start thread = Thread[search-thread-1,5,search-threadgroup]
         * start thread = Thread[search-thread-2,5,search-threadgroup]
         * start thread = Thread[search-thread-3,5,search-threadgroup]
         * start thread = Thread[search-thread-4,5,search-threadgroup]
         * start thread = Thread[search-thread-5,5,search-threadgroup]
         * start thread = Thread[search-thread-6,5,search-threadgroup]
         * start thread = Thread[search-thread-7,5,search-threadgroup]
         * start thread = Thread[search-thread-8,5,search-threadgroup]
         * start thread = Thread[search-thread-9,5,search-threadgroup]
         * Thread[search-thread-0,5,search-threadgroup] begin search
         * Thread[search-thread-1,5,search-threadgroup] begin search
         * Thread[search-thread-2,5,search-threadgroup] begin search
         * Thread[search-thread-3,5,search-threadgroup] begin search
         * Thread[search-thread-4,5,search-threadgroup] begin search
         * Thread[search-thread-5,5,search-threadgroup] begin search
         * Thread[search-thread-6,5,search-threadgroup] begin search
         * Thread[search-thread-7,5,search-threadgroup] begin search
         * Thread[search-thread-8,5,search-threadgroup] begin search
         * Thread[search-thread-9,5,search-threadgroup] begin search
         * activeCount=10
         * activeCount=10
         * activeCount=10
         * activeCount=10
         * activeCount=10
         * Thread[search-thread-0,5,search-threadgroup] end search,耗时：5000
         * activeCount=9
         * Thread[search-thread-1,5,search-threadgroup] end search,耗时：6000
         * activeCount=8
         * Thread[search-thread-2,5,search-threadgroup] end search,耗时：7000
         * activeCount=7
         * Thread[search-thread-3,5,search-threadgroup] end search,耗时：8000
         * activeCount=6
         * Thread[search-thread-4,5,search-threadgroup] end search,耗时：9000
         * activeCount=5
         * Thread[search-thread-5,5,search-threadgroup] end search,耗时：10000
         * activeCount=4
         * 找到了需要的文件，开始终止其他的子线程
         * Thread[search-thread-6,5,search-threadgroup] 被中断
         * Thread[search-thread-9,5,search-threadgroup] 被中断
         * Thread[search-thread-8,5,search-threadgroup] 被中断
         * Thread[search-thread-7,5,search-threadgroup] 被中断
         *
         *
         *
         *
         *
         *
         */

    }

}

class SearchRunnable implements Runnable {

    private int sleepTime;

    public SearchRunnable(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread() + " begin search ");
            Thread.sleep(sleepTime);
            System.out.println(Thread.currentThread() + " end search,耗时：" + this.sleepTime);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread() + " 被中断 ");
        }
    }

}