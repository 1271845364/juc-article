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
public class Demo2 {

    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

    public static class T extends Thread {
        int sleep;

        public T(String name, int sleep) {
            super(name);
            this.sleep = sleep;
        }

        @Override
        public void run() {
            //等待10个人到齐之后吃饭，先到的人坐那等着，什么也不能干
            this.eat();
            //等待10个人到齐之后开车去下一个景点，先到的人坐那等着，什么事情不要干
            this.drive();

        }

        /**
         * 等待所有人到齐之后，开车去下一站
         */
        private void drive() {
            try {
                long startTime = System.currentTimeMillis();
                //调用await()的时候，当前线程将会被阻塞，需要等待其他员工都到达await()才能继续
                cyclicBarrier.await();
                long endTime = System.currentTimeMillis();
                System.out.println(this.getName() + ",sleep:" + this.sleep + " 等待了 " + (endTime - startTime) + " ms，去下一个景点的路上");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        /**
         * 等待吃饭
         */
        private void eat() {
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
         * 员工4,sleep:4 等待了 5999 ms，开始吃饭了
         * 员工1,sleep:1 等待了 8999 ms，开始吃饭了
         * 员工3,sleep:3 等待了 6999 ms，开始吃饭了
         * 员工5,sleep:5 等待了 5000 ms，开始吃饭了
         * 员工2,sleep:2 等待了 7999 ms，开始吃饭了
         * 员工10,sleep:10 等待了 0 ms，开始吃饭了
         * 员工8,sleep:8 等待了 2000 ms，开始吃饭了
         * 员工7,sleep:7 等待了 3000 ms，开始吃饭了
         * 员工6,sleep:6 等待了 4000 ms，开始吃饭了
         * 员工9,sleep:9 等待了 1000 ms，开始吃饭了
         * 员工1,sleep:1 等待了 8999 ms，去下一个景点的路上
         * 员工6,sleep:6 等待了 4000 ms，去下一个景点的路上
         * 员工8,sleep:8 等待了 2000 ms，去下一个景点的路上
         * 员工5,sleep:5 等待了 5000 ms，去下一个景点的路上
         * 员工4,sleep:4 等待了 5999 ms，去下一个景点的路上
         * 员工2,sleep:2 等待了 7999 ms，去下一个景点的路上
         * 员工3,sleep:3 等待了 6999 ms，去下一个景点的路上
         * 员工10,sleep:10 等待了 0 ms，去下一个景点的路上
         * 员工9,sleep:9 等待了 1000 ms，去下一个景点的路上
         * 员工7,sleep:7 等待了 3000 ms，去下一个景点的路上
         *
         * 以上代码中，CyclicBarrier相当于使用了2次，第一次用于等待所有人到达后开饭，第二次用于等待
         * 所有人上车后驱车去下一个景点。注意一些先到的员工会在其他人到达之前，都处于等待状态(CyclicBarrier.await()会让当前线程阻塞)
         * 无法干其他事情，等到最后一个人到了会唤醒所有人，然后继续
         *
         * CyclicBarrier内部相当于有一个计数器（构造方法传入），每次调用await()后，计数器会减1，并且await()方法会让当前线程阻塞，
         * 等待计数器为0的时候，所有在await()上等待的线程被唤醒，然后继续向下执行，此时计数器又会被还原为创建的值，然后可以继续再次使用
         *
         */
    }
}