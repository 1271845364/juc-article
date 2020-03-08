package com.juc.chat10;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/11
 */
public class Demo5 {

    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " start");
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " 被唤醒");
            }
        });

        thread.setName("t1");
        thread.start();
        //休眠5s
        TimeUnit.SECONDS.sleep(5);
        condition.signal();

        /**
         * 输出结果：
         * Exception in thread "t1" 1568207362692:t1 start
         * java.lang.IllegalMonitorStateException
         * 	at java.util.concurrent.locks.ReentrantLock$Sync.tryRelease(ReentrantLock.java:151)
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.release(AbstractQueuedSynchronizer.java:1261)
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer.fullyRelease(AbstractQueuedSynchronizer.java:1723)
         * 	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2036)
         * 	at com.juc.chat10.Demo5$1.run(Demo5.java:23)
         * 	at java.lang.Thread.run(Thread.java:745)
         *
         * 	出现异常，condition.await();和condition.signal();都触发了IllegalMonitorStateException异常。
         * 	原因：调用condition中线程等待和唤醒的方法的前提是必须先获取lock的锁
         */
    }
}