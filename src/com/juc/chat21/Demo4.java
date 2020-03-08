package com.juc.chat21;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

/**
 * Unsafe锁示例
 *
 * Unsafe中提供了和volatile语义一样的功能的方法
 * 设置给定对象的int值，使用volatile语义，即设置后立马更新到内存的主存中，对其他线程可见
 * 参数：
 *  var1：要操作的对象
 *  var2：表示操作对象中的某个字段地址偏移量
 *  var4：将var2对应的字段的值修改为var4，并且立即刷新到主存中
 *  调用这个方法会强制的将工作内存中的修改的数据刷新到主存中
 * public native void putIntVolatile(Object var1, long var2, int var4);
 *
 * 获取给定对象的指定偏移量offset的int值，使用volatile语义，总能获取到最新的int值
 * 参数：
 *  var1：要操作的对象
 *  var2：表示操作对象中的某个字段地址的偏移量
 *  每次调用这个方法就会强制的从主存中读取值，将其复制到工作内存中使用
 * public native int getIntVolatile(Object var1, long var2);
 *
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/30
 */
public class Demo4 {

    private static Unsafe unsafe;

    /**
     * 用来记录网站访问量，每次访问+1
     */
    private static int count;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟访问一次
     */
    private static void request() {
        unsafe.monitorEnter(Demo4.class);
        try {
            count++;
        } finally {
            unsafe.monitorExit(Demo4.class);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int threadCount = 100;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        request();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            });
            thread.start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "，耗时：" + (System.currentTimeMillis() - startTime) + "ms，count=" + count);

        /**
         * 输出结果：
         * main，耗时：85ms，count=1000
         *
         * 1、monitorEnter、monitorExit、tryMonitorEnter三个方法已经过时了，不建议使用了
         * 2、monitorEnter、monitorExit必须成对出现，出现次数必须一致，也就是说锁了n次，也必须释放n次，否则会造成死锁
         *
         */
    }
}