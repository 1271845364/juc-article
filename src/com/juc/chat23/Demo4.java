package com.juc.chat23;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 需求：有一家蛋糕店，为了挽留客户，决定为贵宾卡客户一次性赠送20元，刺激客户充值和消费，但条件是，每一位客户只能被赠送一次，
 * 现在我们用 AtomicStampedReference 来实现这个功能
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/07
 */
public class Demo4 {

    /**
     * 账户余额19元
     */
    private static int accountMoney = 19;

    /**
     * 对账户余额做原子操作
     */
    private static AtomicStampedReference<Integer> money = new AtomicStampedReference<>(accountMoney,0);

    /**
     * 模拟两个线程同时更新后台数据库，为用户充值
     */
    private static void recharge() {
        for (int i = 0; i < 2; i++) {
            int stamp = money.getStamp();
            new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    Integer m = money.getReference();
                    if (m == accountMoney) {
                        if (money.compareAndSet(m, m + 20,stamp,stamp+1)) {
                            System.out.println("当前时间戳：" + money.getStamp() + "，当前余额：" + m + "，小于20，充值20元成功，余额：" + money.getReference() + "元");
                        }
                    }
                    //休眠100ms
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 模拟用户消费
     */
    private static void consume() {
        for (int i = 0; i < 50; i++) {
            Integer m = money.getReference();
            int stamp = money.getStamp();
            if (m > 20) {
                if (money.compareAndSet(m, m - 20,stamp,stamp+1)) {
                    System.out.println("当前时间戳：" + money.getStamp() +"，当前余额：" + m + "，大于20，成功消费20元，余额：" + money.getReference() + "元");
                }
            }
            //休眠50ms
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        recharge();
        consume();

        /**
         * 输出结果：
         * 当前时间戳：1，当前余额：19，小于20，充值20元成功，余额：39元
         * 当前时间戳：2，当前余额：39，大于20，成功消费20元，余额：19元
         *
         * 关于这个时间戳的，在数据库修改数据中也有类似的用法，比如2个编辑同时编辑一篇文章，
         * 同时提交，只允许一个用户提交成功，提示另外一个用户：博客已被其他人修改，如何实现呢？
         *
         * 博客表：t_blog(id,content,stamp),stamp默认为0，每次更新+1
         * A、B两个编辑同时对一篇文章进行编辑，stamp都为0，当点击提交的时候，将stamp和id作为条件更新博客内容，执行sql如下：
         * update t_blog set content = 更新的内容,stamp = stamp+1 where id = 博客id and stamp = 0;
         * 这条update会返回影响的行数，只有返回是1，表示更新成功，另外一个提交者返回0，表示需要修改的数据已经不满足条件了，
         * 被其他用户修改了。乐观锁
         *
         *
         */
    }
}