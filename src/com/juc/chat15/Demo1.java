package com.juc.chat15;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 公司组织旅游，大家都有经历过，10个人，中午到饭点了，需要等到10个人都到了才能开饭，先到的人坐那等着
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class Demo1 {

    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

    public static class T extends Thread {
        int sleep;

        public T(String name, int sleep) {
            super(name);
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                //模拟休眠
                TimeUnit.SECONDS.sleep(sleep);
                long startTime = System.currentTimeMillis();
                //调用await()的时候，当前线程将会被阻塞，需要等待其他员工都到达await()才能继续
                cyclicBarrier.await();
                long endTime = System.currentTimeMillis();
                System.out.println(this.getName() + ",sleep:" + this.sleep + " 等待了 " + (endTime - startTime) + " ms");
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
         * 员工3,sleep:3 等待了 6999 ms
         * 员工4,sleep:4 等待了 5999 ms
         * 员工10,sleep:10 等待了 0 ms
         * 员工7,sleep:7 等待了 3000 ms
         * 员工2,sleep:2 等待了 7999 ms
         * 员工6,sleep:6 等待了 4000 ms
         * 员工1,sleep:1 等待了 8999 ms
         * 员工5,sleep:5 等待了 4999 ms
         * 员工9,sleep:9 等待了 1000 ms
         * 员工8,sleep:8 等待了 2000 ms
         *
         * 模拟10个员工上桌吃饭的场景，等待所有员工都到齐了才能开饭，可以看到第10个员工
         * 最慢，前面的都在等待第10个员工，员工1等待了9s，上面代码中调用cyclicBarrier.await();
         * 会让当前线程等待。当10个员工都调用了cyclicBarrier.await();之后，所有处于等待的线程
         * 都会被唤醒，然后继续执行
         *
         */
    }
}