package com.juc.chat15;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 公司组织旅游，大家都有经历过，10个人，中午到饭点了，需要等到10个人都到了才能开饭，先到的人坐那等着，第十个人到了给我们大家上酒
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo3 {

    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {
        //模拟倒酒
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "说，不还意思，让大家久等了，给大家倒酒赔罪！");
    });

    public static class T extends Thread {
        int sleep;

        public T(String name, int sleep) {
            super(name);
            this.sleep = sleep;
        }

        @Override
        public void run() {
            //等待10个人到齐之后吃饭，先到的人坐那等着，什么也不能干
            try {
                //模拟休眠
                TimeUnit.SECONDS.sleep(sleep);
                long startTime = System.currentTimeMillis();
                //调用await()的时候，当前线程将会被阻塞，需要等待其他员工都到达await()才能继续
                cyclicBarrier.await();
                long endTime = System.currentTimeMillis();
                System.out.println(this.getName() + ",sleep:" + this.sleep + " 等待了 " + (endTime - startTime) + " ms，开始吃饭了");

                //休眠sleep时间，模拟当前员工吃饭耗时
                TimeUnit.SECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            new T("员工" + i, i).start();
        }

        /**
         * 输出结果：
         * 员工10说，不还意思，让大家久等了，给大家倒酒赔罪！
         * 员工10,sleep:10 等待了 2000 ms，开始吃饭了
         * 员工1,sleep:1 等待了 11003 ms，开始吃饭了
         * 员工2,sleep:2 等待了 10003 ms，开始吃饭了
         * 员工5,sleep:5 等待了 7002 ms，开始吃饭了
         * 员工4,sleep:4 等待了 8002 ms，开始吃饭了
         * 员工3,sleep:3 等待了 9003 ms，开始吃饭了
         * 员工9,sleep:9 等待了 3001 ms，开始吃饭了
         * 员工8,sleep:8 等待了 4001 ms，开始吃饭了
         * 员工7,sleep:7 等待了 5001 ms，开始吃饭了
         * 员工6,sleep:6 等待了 6002 ms，开始吃饭了
         *
         * 创建CyclicBarrier对象时，多传入一个参数(内部是倒酒操作)，先到的人先等待，待所有人都到齐之后，需要先给大家倒酒，
         * 然后唤醒所有等待的线程。从输出结果中可以看出，倒酒操作是最后一个人操作的，最后一个人倒完酒，才唤醒所有等待的线程
         *
         *
         */
    }
}