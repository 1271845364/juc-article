package com.juc.chat21;

import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * Unsafe中线程调度相关方法
 * 取消阻塞线程
 * public native void unpark(Object var1);
 * 阻塞线程，var1是否是绝对时间，var2是纳秒
 * public native void park(boolean var1, long var2);
 * 获得对象锁(可重入锁)
 * public native void monitorEnter(Object var1);
 * 释放对象锁
 * public native void monitorExit(Object var1);
 * 尝试获取对象锁
 * public native boolean tryMonitorEnter(Object var1);
 *
 * 调用park后，线程将被阻塞，直到unpark调用或者超时，如果之前调用过unpark不会进行阻塞，即park和unpark不区分先后顺序
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/30
 */
public class Demo3 {

    /**
     * park和unpark示例
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        m1();
        m2();
    }

    /**
     * park和unpark实现线程挂起和唤醒
     *
     * 如果在park之前调用了unpark，不会阻塞。park方法被唤醒后，许可又会被置为0，多次调用unpark方法效果是一样的许可还是1
     */
    private static void m1() throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "，start");
            unsafe.park(false, 0);
            System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "，end");
        });
        thread.setName("thread1");
        thread.start();

        TimeUnit.SECONDS.sleep(5);
        unsafe.unpark(thread);
    }

    private static void m2() {
        Thread thread = new Thread(()->{
            System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "，start");
            //如果是0，超时时间是无穷大，永远不会结束
            unsafe.park(false, 0);
            System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "，end");
        });
        thread.setName("thread2");
        thread.start();
    }

    private static Unsafe unsafe;

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
}