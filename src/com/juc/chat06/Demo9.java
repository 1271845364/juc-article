package com.juc.chat06;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock锁申请等待限时
 * 申请锁等待限时是什么意思？一般情况下，获取锁的时间我们是不知道的，synchronized关键字获取锁的过程中，
 * 只能等待其他线程把锁释放之后才能够有机会获取到所。所以获取锁的时间有长有短。如果获取锁的时间能够设置超时时间，那就非常好了。
 * <p>
 * ReentrantLock刚好提供了这样功能，给我们提供了获取锁限时等待的方法 tryLock()，可以选择传入时间参数，
 * 表示等待指定的时间，无参则表示立即返回锁申请的结果：true表示获取锁成功，false表示获取锁失败。
 *
 *
 * 关于tryLock()方法和tryLock(long timeout, TimeUnit unit)方法，说明一下：
 * 1、都会返回boolean值，结果表示获取锁成功或失败
 * 2、tryLock()方法，不管获取锁是否成功，都会立即返回；而有参数的tryLock方法会尝试在指定时间内去获取锁，中间会阻塞的现象，在指定时间之后不管
 * 是否获取锁都会返回结果
 * 3、tryLock()方法不会响应线程的中断方法，而有参数的tryLock方法会响应线程的中断方法，而出发 InterruptedException异常，这个从2个方法的声明上可以可以看出
 *
 *
 * ReentrantLock其他常用的方法
 * 1、isHeldByCurrentThread：实例方法，判断当前线程是否持有ReentrantLock的锁
 *
 * 获取锁的4种方法对比
 * 获取锁的方法	        是否立即响应(不会阻塞)	是否响应中断
 * lock()	                ×	                ×
 * lockInterruptibly()	    ×	                √
 * tryLock()	            √	                ×
 * tryLock(long timeout, TimeUnit unit)	×	    √
 *
 *
 * 总结：
 * RentrantLock可以实现公平锁和非公平锁
 * ReentrantLock默认实现的是非公平锁
 * ReentrantLock的获取锁和释放锁必须成对出现，锁了几次，也要释放几次
 * 释放锁的操作必须放在finally中执行
 * lockInterruptibly()实例方法可以响应线程的中断方法，调用线程的interrupt()方法时，lockInterruptibly()方法会触发 InterruptedException异常
 *  关于 InterruptedException异常说一下，看到方法声明上带有 throws InterruptedException，表示该方法可以响应线程中断，
 *  调用线程的interrupt()方法时，这些方法会触发 InterruptedException异常，触发InterruptedException时，线程的中断中断状态会被清除。
 *  所以如果程序由于调用interrupt()方法而触发 InterruptedException异常，线程的标志由默认的false变为ture，然后又变为false
 * 实例方法tryLock()获会尝试获取锁，会立即返回，返回值表示是否获取成功
 * 实例方法tryLock(long timeout, TimeUnit unit)会在指定的时间内尝试获取锁，指定的时间内是否能够获取锁，都会返回，返回值表示是否获取锁成功，该方法会响应线程的中断
 *
 *
 *
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/05
 */
public class Demo9 {


    private static ReentrantLock lock = new ReentrantLock();

    public static class T extends Thread{
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            //ReentrantLock public boolean tryLock(long timeout, TimeUnit unit)
            //            throws InterruptedException 该方法在指定的时间内不管是否可以获取锁，都会返回结果，返回true，表示获取锁成功，返回false表示获取失败
            try{
                System.out.println(System.currentTimeMillis() + ":"+Thread.currentThread().getName() + ":开始获取锁！");
                //获取锁超时时间设置为3秒，3秒内是否能否获取锁都会返回
                if(lock.tryLock(3,TimeUnit.SECONDS)) {
                    System.out.println(System.currentTimeMillis() + ":"+Thread.currentThread().getName() + ":获取到了锁！");
                    //获取到锁之后，休眠5s
                    TimeUnit.SECONDS.sleep(5);
                }else{
                    System.out.println(System.currentTimeMillis() + ":"+Thread.currentThread().getName() + ":未能获取到锁！");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if(lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) {
        T t1 = new T("t1");
        T t2 = new T("t2");
        t1.start();
        t2.start();
        //程序中调用了ReentrantLock的实例方法tryLock(3,TimeUnit.SECONDS),
        //表示获取锁的超时时间是3秒，3秒后不管是否能否获取锁，该方法都会有返回值，获取到锁之后，内部休眠了5秒，会导致另外一个线程获取锁失败
        /**
         * 1567691062250:t2:开始获取锁！
         * 1567691062250:t1:开始获取锁！
         * 1567691062251:t2:获取到了锁！
         * 1567691065253:t1:未能获取到锁！
         *
         * 输出结果中分析，t2获取到锁了，然后休眠了5秒，t1获取锁失败，t1打印了2条信息，时间相差3秒左右。
         *
         */


    }

}