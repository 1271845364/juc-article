package com.juc.chat13;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 有3个人参见跑步比赛，需要先等指令员发指令枪后才能开跑，所有人都跑完之后，指令员喊一声，大家跑完了。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo4 {

    public static class T extends Thread {
        //跑步时长s
        int runCostSeconds;
        CountDownLatch commandCd;
        CountDownLatch countDownLatch;

        public T(String name, int runCostSeconds, CountDownLatch commandCd, CountDownLatch countDownLatch) {
            super(name);
            this.runCostSeconds = runCostSeconds;
            this.commandCd = commandCd;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            //等待指令枪响
            try {
                commandCd.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread thread = Thread.currentThread();
            long startTime = System.currentTimeMillis();
            System.out.println(startTime + "," + thread.getName() + ",开始跑");

            try {
                //模拟耗时操作，休眠runCostSeconds
                TimeUnit.SECONDS.sleep(runCostSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime + "," + thread.getName() + ",跑步结束，耗时：" + (endTime - startTime) + " ms");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "线程 start");
        CountDownLatch commandCd = new CountDownLatch(1);
        CountDownLatch countDownLatch = new CountDownLatch(3);

        long startTime = System.currentTimeMillis();
        T t1 = new T("小张", 2, commandCd, countDownLatch);
        t1.start();

        T t2 = new T("小李", 5, commandCd, countDownLatch);
        t2.start();

        T t3 = new T("路人甲", 10, commandCd, countDownLatch);
        t3.start();

        //主线程休眠5s，模拟指令员发枪耗时操作
        TimeUnit.SECONDS.sleep(5);
        System.out.println(System.currentTimeMillis() + "，枪响了，大家开始跑");
        commandCd.countDown();

        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "所有人跑完了，主线程耗时：" + (endTime - startTime) + " ms");

        /**
         * 输出结果：
         * 1568720802360,main线程 start
         * 1568720807375，枪响了，大家开始跑
         * 1568720807375,路人甲,开始跑
         * 1568720807375,小张,开始跑
         * 1568720807375,小李,开始跑
         * 1568720809391,小张,跑步结束，耗时：2016 ms
         * 1568720812390,小李,跑步结束，耗时：5015 ms
         * 1568720817390,路人甲,跑步结束，耗时：10015 ms
         * 1568720817390,main所有人跑完了，主线程耗时：15030 ms
         *
         * 代码中，t1、t2、t3线程启动之后，都阻塞在commandCd.await();主线程模拟发枪准备操作耗时5s，
         * 然后调用commandCd.countDown();模拟发枪操作，此方法被调用以后，阻塞在commandCd.await()的线程
         * 会继续执行。主线程调用 countDownLatch.await();之后进行等待，每个人跑完之后，调用 countDownLatch.countDown();
         * 通知一下countDownLatch让计数器减1，最后三个人都跑完了，主线程从 countDownLatch.await();返回继续向下执行。
         *
         */
    }
}