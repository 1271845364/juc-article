package com.juc.chat06;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock锁申请等待限时
 * 申请锁等待限时是什么意思？一般情况下，获取锁的时间我们是不知道的，synchronized关键字获取锁的过程中，
 * 只能等待其他线程把锁释放之后才能够有机会获取到锁。所以获取锁的时间有长有短。如果获取锁的时间能够设置超时时间，那就非常好了。
 * <p>
 * ReentrantLock刚好提供了这样功能，给我们提供了获取锁限时等待的方法 tryLock()，可以选择传入时间参数，
 * 表示等待指定的时间，无参则表示立即返回锁申请的结果：true表示获取锁成功，false表示获取锁失败。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/05
 */
public class Demo8 {

    private static ReentrantLock lock = new ReentrantLock();

    public static class T extends Thread{
        public T(String name) {
            super(name);
        }

        @Override
        public void run() {
            //ReentrantLock tryLock()方法会立刻返回，true表示获取锁成功，false表示没有获取到锁
            try{
                System.out.println(System.currentTimeMillis() + ":"+Thread.currentThread().getName() + ":开始获取锁！");
                if(lock.tryLock()) {
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
        //可以看到t2获取成功，t1获取失败了，tryLock()是立即响应的，中间不会有阻塞。
    }

}