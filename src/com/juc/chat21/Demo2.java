package com.juc.chat21;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unsafe中的CAS操作
 * <p>
 * CAS操作
 * 参数：
 * var1：包含要修改field的对象
 * var2：对象中某field的偏移量
 * var4：期待值
 * var5：更新值
 * public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);
 * public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);
 * public final native boolean compareAndSwapLong(Object var1, long var2, long var4, long var6);
 * <p>
 * 什么是CAS，比较并替换，实现并发算法。
 * CAS操作包含三个操作数：内存位置、预期值、新值。执行CAS操作的时候，将内存位置的值与预期原值比较，如果相等，那么处理器会
 * 自动将该位置的值更新为新值，否则，处理器什么也不做，多个线程同时执行cas，只会有一个成功。
 * CAS是一条CPU原子指令
 * <p>
 * unsafe提供的cas方法，给cpu发一条指令的时候，会判断当前系统是否为多核系统，如果是多核系统，
 * 给总线加锁，只有一个线程会对总线加锁成功，加锁成功之后执行cas操作
 * <p>
 * CAS的原子性操作是CPU实现的
 * <p>
 * <p>
 * <p>
 * Unsafe中原子操作相关方法介绍
 * int类型值原子操作，对var2地址对应的值做原子增加操作(+var4)
 * public final int getAndAddInt(Object var1, long var2, int var4) {
 * int var5;
 * do {
 * var5 = this.getIntVolatile(var1, var2);
 * } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
 * <p>
 * return var5;
 * }
 * <p>
 * long类型值原子操作，对var2地址对应的值做原子增加操作(+var4)
 * public final long getAndAddLong(Object var1, long var2, long var4) {
 * long var6;
 * do {
 * var6 = this.getLongVolatile(var1, var2);
 * } while(!this.compareAndSwapLong(var1, var2, var6, var6 + var4));
 * <p>
 * return var6;
 * }
 * <p>
 * int类型值做原子操作，将var2地址对应的位置为var4
 * public final int getAndSetInt(Object var1, long var2, int var4) {
 * int var5;
 * do {
 * var5 = this.getIntVolatile(var1, var2);
 * } while(!this.compareAndSwapInt(var1, var2, var5, var4));
 * <p>
 * return var5;
 * }
 * <p>
 * long类型做原子操作，将var2地址对应的位置为var4
 * public final long getAndSetLong(Object var1, long var2, long var4) {
 * long var6;
 * do {
 * var6 = this.getLongVolatile(var1, var2);
 * } while(!this.compareAndSwapLong(var1, var2, var6, var4));
 * <p>
 * return var6;
 * }
 * <p>
 * Object类型值原子操作方法，将var2地址对应的位置设置为var4
 * public final Object getAndSetObject(Object var1, long var2, Object var4) {
 * Object var5;
 * do {
 * var5 = this.getObjectVolatile(var1, var2);
 * } while(!this.compareAndSwapObject(var1, var2, var5, var4));
 * <p>
 * return var5;
 * }
 * <p>
 * 上面的内容都是通过CAS自旋的方式实现的，这些方法在多线程情况下都保证原子性、正确性
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/30
 */
public class Demo2 {
    /**
     * 来个示例，我们还是来实现一个网站计数功能，同时有100个人发起对网站的请求，每个人发起10次请求，每次请求算一次，最终结果是1000次
     *
     * @param args
     */

    private static Unsafe unsafe;

    /**
     * 记录网站访问量
     */
    private static int count;

    /**
     * count在Demo2.class中的地址的偏移量
     */
    private static long countOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            Field countField = Demo2.class.getDeclaredField("count");
            //获取count字段在Demo2中的内存地址的偏移量
            countOffset = unsafe.staticFieldOffset(countField);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void request() throws InterruptedException {
        //模拟访问耗时5s
        TimeUnit.MILLISECONDS.sleep(5);
        //对count+1
        unsafe.getAndAddInt(Demo2.class, countOffset, 1);
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int threadCount = 100;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(()->{
                try {
                    for(int j=0;j<10;j++){
                        request();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    countDownLatch.countDown();
                }
            });
            thread.start();

        }

        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "，耗时：" + (System.currentTimeMillis() - startTime) + "ms，count=" + count);

        /**
         * 输出结果：
         * main，耗时：113ms，count=1000
         *
         * 采用静态代码块中使用反射获取Unsafe类的实例，然后获取Demo2中count字段内存地址偏移量countOffset，
         * main方法中模拟了100人，每人10次请求，等到所有请求完毕之后，输出count
         * 通过CountDownLatch.await()让主线程等待，等待100个子线程都执行完毕之后，主线程在继续执行。
         *
         *
         */
    }
}