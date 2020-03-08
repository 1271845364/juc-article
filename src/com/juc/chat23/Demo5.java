package com.juc.chat23;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 如果需要原子更新某个类里的某个字段时，需要用到对象的属性修改原子类。
 * AtomicIntegerFieldUpdater：原子更新整形字段的值
 * AtomicLongFieldUpdater：原子更新长整形字段的值
 * AtomicReferenceFieldUpdater ：原子更新引用类型字段的值
 * <p>
 * 参数说明：
 * tclass：需要操作的字段所在的类
 * vclass：操作字段的类型
 * fieldName：字段名称
 * public static <U, W> AtomicReferenceFieldUpdater<U, W> newUpdater(Class<U> tclass, Class<W> vclass, String fieldName)
 * <p>
 * <p>
 * 需求：多线程并发调用一个类的初始化方法，如果未被初始化过，将执行初始化工作，要求只能初始化一次
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/08
 */
public class Demo5 {

    private static Demo5 demo5 = new Demo5();

    /**
     * 用来标注是否被初始化过
     */
    private volatile Boolean isInit = Boolean.FALSE;

    AtomicReferenceFieldUpdater<Demo5, Boolean> updater =
            AtomicReferenceFieldUpdater.newUpdater(Demo5.class, Boolean.class, "isInit");

    /**
     * 模拟初始化工作
     */
    public void init() throws InterruptedException {
        //isInit为false的时候，才进行初始化，并将isInit采用原子操作置为true
        if (updater.compareAndSet(demo5, Boolean.FALSE, Boolean.TRUE)) {
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "，开始初始化！");
            //模拟休眠3s
            TimeUnit.SECONDS.sleep(3);
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "，初始化完毕！");
        } else {
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + "，有其他线程已经执行了初始化");
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    demo5.init();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        /**
         * 输出结果：
         * 1570535363471,Thread-0，开始初始化！
         * 1570535363473,Thread-1，有其他线程已经执行了初始化
         * 1570535363473,Thread-2，有其他线程已经执行了初始化
         * 1570535363474,Thread-4，有其他线程已经执行了初始化
         * 1570535363475,Thread-3，有其他线程已经执行了初始化
         * 1570535366500,Thread-0，初始化完毕！
         *
         * 1、isInit属性必须要volatile修饰，可以确保变量的可见性
         * 2、可以看出多线程同时执行init()方法，只有一个线程执行了初始化操作，其他线程跳过了。
         * 多个线程同时到达updater.compareAndSet，只有一个会成功
         *
         */
    }

}