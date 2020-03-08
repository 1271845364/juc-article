package com.juc.chat10;

import java.util.concurrent.locks.LockSupport;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/16
 */
public class Demo10 {

    static class BlockerDemo{
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            LockSupport.park();
        });
        t1.setName("t1");
        t1.start();

        Thread t2 = new Thread(()->{
            LockSupport.park(new BlockerDemo());
        });
        t2.setName("t2");
        t2.start();

        /**
         * "t2" #12 prio=5 os_prio=0 tid=0x0000000019b4c800 nid=0x111c waiting on condition [0x000000001a42f000]
         *    java.lang.Thread.State: WAITING (parking)
         *         at sun.misc.Unsafe.park(Native Method)
         *         - parking to wait for  <0x00000000d6002d58> (a com.juc.chat10.Demo1$BlockerDemo)
         *         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
         *         at com.juc.chat10.Demo1.lambda$main$1(Demo1.java:22)
         *         at com.juc.chat10.Demo1$$Lambda$2/990368553.run(Unknown Source)
         *         at java.lang.Thread.run(Thread.java:745)
         *
         * "t1" #11 prio=5 os_prio=0 tid=0x0000000019b4a000 nid=0x1640 waiting on condition [0x000000001a32f000]
         *    java.lang.Thread.State: WAITING (parking)
         *         at sun.misc.Unsafe.park(Native Method)
         *         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:304)
         *         at com.juc.chat10.Demo1.lambda$main$0(Demo1.java:16)
         *         at com.juc.chat10.Demo1$$Lambda$1/295530567.run(Unknown Source)
         *         at java.lang.Thread.run(Thread.java:745)
         *
         *  上面是jstack查看的线程堆栈信息
         *  线程t1和线程t2的不同点是，t2中调用park方法传入了一个BlockerDemo对象，从上面的线程堆栈信息中，
         *  发现t2线程的堆栈信息中多了一行- parking to wait for  <0x00000000d6002d58> (a com.juc.chat10.Demo1$BlockerDemo)
         *  刚好是传入的BlockerDemo对象，park传入的这个参数可以让我们在线程堆栈信息中方便排查问题，其他暂无作用
         *
         *
         *  LockSupport的其他等待方法，包含有超时时间了，过了超时时间，等待方法会自动返回，让线程继续运行
         *
         *  3种让线程等待和唤醒的方法
         *  1、Object的wait/notify/notifyAll
         *  2、juc中的Condition的await/signal/signalAll
         *  3、juc中的LockSupport提供的park/unpark
         */

    }
}