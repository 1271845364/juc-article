package com.juc.chat15;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 公司组织旅游，大家都有经历过，10个人，中午到饭点了，需要等到10个人都到了才能开饭
 * 员工1只愿意等待5s，5s没到齐自己要开吃了，只有员工1特例，其他的人还是得按照规则来
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/19
 */
public class Demo6 {

    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

    private static boolean guize = false;

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
                if (!guize) {
                    if ("员工1".equals(this.getName())) {
                        cyclicBarrier.await(5, TimeUnit.SECONDS);
                    } else {
                        cyclicBarrier.await();
                    }
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

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            T t = new T("员工" + i, i);
            t.start();
        }

        //等待15s之后，重置，重建规则
        TimeUnit.SECONDS.sleep(15);

        cyclicBarrier.reset();
        guize = true;
        System.out.println("----------- 请大家按照规则来 ----------");
        //再来一次
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
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工4,sleep:4 等待了 2005 ms
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工2,sleep:2 等待了 4004 ms
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工6,sleep:6 等待了 5 ms
         * java.util.concurrent.TimeoutException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:257)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:435)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:39)
         * 员工1,sleep:1 等待了 5005 ms
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * java.util.concurrent.BrokenBarrierException
         * 员工3,sleep:3 等待了 3007 ms	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         *
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工5,sleep:5 等待了 1008 ms
         * 员工7 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工7,sleep:7 等待了 0 ms
         * 员工8 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工8,sleep:8 等待了 0 ms
         * 员工9 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工9,sleep:9 等待了 0 ms
         * 员工10 到了
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo6$T.run(Demo6.java:41)
         * 员工10,sleep:10 等待了 0 ms
         * ----------- 请大家按照规则来 ----------
         * 员工1 到了
         * 员工2 到了
         * 员工3 到了
         * 员工4 到了
         * 员工5 到了
         * 员工6 到了
         * 员工7 到了
         * 员工8 到了
         * 员工9 到了
         * 员工10 到了
         * 员工10,sleep:10 等待了 0 ms
         * 员工1,sleep:1 等待了 9001 ms
         * 员工5,sleep:5 等待了 5003 ms
         * 员工7,sleep:7 等待了 3002 ms
         * 员工9,sleep:9 等待了 1001 ms
         * 员工4,sleep:4 等待了 6002 ms
         * 员工3,sleep:3 等待了 7001 ms
         * 员工2,sleep:2 等待了 8001 ms
         * 员工8,sleep:8 等待了 2002 ms
         * 员工6,sleep:6 等待了 4001 ms
         *
         *
         * 第一次规则被打乱了，过了一会重建规则(cyclicBarrier.reset())，接着又重来了一次模拟等待吃饭的操作，正常了
         *
         *
         *        CountDownLatch和CyclicBarrier的区别
         * 主管相当于CountDownLatch，干活的小弟相当于做事情的线程
         * 老板交给主管一个任务，让主管搞完之后立即上报给老板。主管下面有10个小弟，接到任务之后将任务划分为10个小的任务分给
         * 每个小弟去干，主管一直处于等待状态(主管会调用await()，此方法会阻塞当前线程)，让每个小弟干完之后通知一下主管(调用countDown()方法通知主管，
         * 此方法会立即返回)，主管等到所有小弟都做完了，会被唤醒，从await()方法上苏醒，然后将结果反馈给老板。期间主管会等待，
         * 会等待所有小弟将结果汇报给自己。
         *
         * CyclicBarrier是一批线程让自己等待，等待所有的线程都准备好了，自己才能继续
         *
         *
         *
         */
    }
}