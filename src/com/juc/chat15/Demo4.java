package com.juc.chat15;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 公司组织旅游，大家都有经历过，10个人，中午到饭点了，需要等到10个人都到了才能开饭，先到的人坐那等着，第五个人突然接了一个电话，有事，得先吃了
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo4 {

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
            //等待10个人到齐之后吃饭，先到的人坐那等着，什么也不能干
            try {
                //模拟休眠
                TimeUnit.SECONDS.sleep(sleep);
                startTime = System.currentTimeMillis();
                //调用await()的时候，当前线程将会被阻塞，需要等待其他员工都到达await()才能继续
                System.out.println(this.getName() + "到了！");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            endTime = System.currentTimeMillis();
            System.out.println(this.getName() + "，sleep:" + this.sleep + " 等待了" + (endTime - startTime) + " ms，开始吃饭了！");
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            int sleep = 0;
            if (i == 10) {
                sleep = 10;
            }
            Thread t = new T("员工" + i, sleep);
            t.start();

            if (i == 5) {
                try {
                    //模拟员工5接了一个电话，将自己等待吃饭给打断了，自己先吃了
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(t.getName() + "，有点急事，我先开干了！");
                    t.interrupt();
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 输出结果：
         * 员工1到了！
         * 员工2到了！
         * 员工3到了！
         * 员工5到了！
         * 员工4到了！
         * 员工5，有点急事，我先开干了！
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * 员工1，sleep:0 等待了1004 ms，开始吃饭了！
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * 员工3，sleep:0 等待了1005 ms，开始吃饭了！
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * 员工4，sleep:0 等待了1005 ms，开始吃饭了！
         * java.lang.InterruptedException
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.reportInterruptAfterWait(AbstractQueuedSynchronizer.java:2014)
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2048)
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:234)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * java.util.concurrent.BrokenBarrierException员工5，sleep:0 等待了1006 ms，开始吃饭了！
         *
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:250)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * 员工2，sleep:0 等待了1006 ms，开始吃饭了！
         * 员工6到了！
         * 员工7到了！
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 员工8到了！	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         *
         * 员工9到了！
         * 员工7，sleep:0 等待了1 ms，开始吃饭了！
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)员工6，sleep:0 等待了1 ms，开始吃饭了！
         *
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * 员工9，sleep:0 等待了2 ms，开始吃饭了！
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * 员工8，sleep:0 等待了3 ms，开始吃饭了！
         * 员工10到了！
         * java.util.concurrent.BrokenBarrierException
         * 	at java.util.concurrent.CyclicBarrier.dowait(CyclicBarrier.java:207)
         * 	at java.util.concurrent.CyclicBarrier.await(CyclicBarrier.java:362)
         * 	at com.juc.chat15.Demo4$T.run(Demo4.java:35)
         * 员工10，sleep:10 等待了1 ms，开始吃饭了！
         *
         *
         *
         * 从输出结果看，员工5接到电话有急事，先吃了
         * 前面4个员工都在await()处等待着，员工5也在await()等待着，等了1s，接了个电话，然后员工5
         * 发送中断信号后(t.interrupt(););员工5的await()方法会触发InterruptedException异常，
         * 此时其他等待中的前4个员工，看着5开吃了，自己立即也不等了，内部从await()方法中触发BrokenBarrierException
         * 异常，然后也开吃了，后面的6/7/8/9/10员工来了以后发现大家都开吃了，自己也不等了，6-10号员工调用await()直接抛出了
         * BrokenBarrierException异常，然后继续向下
         *
         *
         *
         * 结论：
         * 1、内部有一个人把规则破坏了(接收到中断信号)，其他人都不按规则来了，不会等待了
         * 2、接收到中断信号的线程，await方法会触发中断InterruptedException异常，然后被唤醒向下运行
         * 3、其他等待中或者后面到达的线程，会在await方法上触发BrokenBarrierException异常，然后继续执行
         *
         *
         *
         */
    }
}