package com.juc.chat15;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 公司组织旅游，大家都有经历过，10个人，中午到饭点了，需要等到10个人都到了才能开饭
 * 员工1只愿意等待5s，5s没到齐自己要开吃了
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo5 {

    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

    public static class T extends Thread {
        int sleep;

        public T(String name, int sleep) {
            super(name);
            this.sleep = sleep;
        }

        @Override
        public void run() {
            long startTime = 0, endTime = 0;
            try {
                //模拟休眠
                TimeUnit.SECONDS.sleep(sleep);
                startTime = System.currentTimeMillis();
                System.out.println(this.getName() + " 到了");
                if ("员工1".equals(this.getName())) {
                    cyclicBarrier.await(5, TimeUnit.SECONDS);
                } else {
                    //调用await()的时候，当前线程将会被阻塞，需要等待其他员工都到达await()才能继续
                    cyclicBarrier.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            endTime = System.currentTimeMillis();
            System.out.println(this.getName() + ",sleep:" + this.sleep + " 等待了 " + (endTime - startTime) + " ms");
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            T t = new T("员工" + i, i);
            t.start();
        }

        /**
         * 输出结果：
         * 员工1 到了
         * 员工2 到了
         * 员工3 到了
         * 员工4 到了
         * 员工5 到了
         * 员工6 到了
         * java.util.concurrent.TimeoutException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:257)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:435)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:36)
         * 员工1,sleep:1 等待了 5005 ms
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * 员工6,sleep:6 等待了 5 msjava.util.concurrent.BrokenBarrierException
         *
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * 员工4,sleep:4 等待了 2006 ms
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * java.util.concurrent.BrokenBarrierException员工3,sleep:3 等待了 3007 ms
         *
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * java.util.concurrent.BrokenBarrierException员工5,sleep:5 等待了 1007 ms
         *
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * 员工2,sleep:2 等待了 4008 ms
         * 员工7 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * 员工7,sleep:7 等待了 0 ms
         * 员工8 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * 员工8,sleep:8 等待了 0 ms
         * 员工9 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * 员工9,sleep:9 等待了 0 ms
         * 员工10 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo5$T.run(Demo5.java:39)
         * 员工10,sleep:10 等待了 0 ms
         *
         *
         * 从输出结果中可以看出，员工1等待5s，开吃了，其他等待的人开吃了，后面来的人不等待，直接开吃
         *
         * 员工1调用await()方法等待5s之后，触发了TimeoutException异常，然后继续向下运行，其他的
         * 员工在员工1开吃之前已经等待了一会的几个员工，他们看到员工1开吃了，自己也不等待了，立即吃，
         * 还有几个员工在1开吃之后到达的，他们直接不等待了，直接抛出BrokenBarrierException异常，
         * 然后也开吃了
         *
         *
         * 结论：
         * 1、等待超时的方法await(long timeout,TimeUnit unit) throws InterruptedException,
         *                BrokenBarrierException,
         *                TimeoutException
         * 2、内部有一个人把规则破坏了(等待超时)，其他人都不按规则来了，不会等待了
         * 3、等待超时的线程，await()方法会触发TimeoutException异常，然后被唤醒向下运行
         * 4、其他等待中或者后面到达的线程，会在await()方法上触发BrokenBarrierException异常，
         * 然后继续执行
         *
         *
         */
    }
}