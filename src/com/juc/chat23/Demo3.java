package com.juc.chat23;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 需求：有一家蛋糕店，为了挽留客户，决定为贵宾卡客户一次性赠送20元，刺激客户充值和消费，但条件是，每一位客户只能被赠送一次，
 * 现在我们用AtomicReference来实现这个功能
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/07
 */
public class Demo3 {

    /**
     * 账户余额19元
     */
    private static int accountMoney = 19;

    /**
     * 对账户余额做原子操作
     */
    private static AtomicReference<Integer> money = new AtomicReference<>(accountMoney);

    /**
     * 模拟两个线程同时更新后台数据库，为用户充值
     */
    private static void recharge() {
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    Integer m = money.get();
                    if (m == accountMoney) {
                        if (money.compareAndSet(m, m + 20)) {
                            System.out.println("当前余额：" + m + "，小于20，充值20元成功，余额：" + money.get());
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
        for (int i = 0; i < 5; i++) {
            Integer m = money.get();
            if (m > 20) {
                if (money.compareAndSet(m, m - 20)) {
                    System.out.println("当前余额：" + m + "，大于20，成功消费20元，余额：" + money.get());
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
         * 当前余额：19，小于20，充值20元成功，余额：39
         * 当前余额：39，大于20，成功消费20元，余额：19
         * 当前余额：19，小于20，充值20元成功，余额：39
         * 当前余额：39，大于20，成功消费20元，余额：19
         * 当前余额：19，小于20，充值20元成功，余额：39
         * 当前余额：39，大于20，成功消费20元，余额：19
         * 当前余额：19，小于20，充值20元成功，余额：39
         *
         * 这个账户被反复多次充值。原因是账户余额被反复修改，修改后的值和原有的数值19一样，使得
         * CAS操作无法正确判断当前数据是否被修改过(是否被加过20)。虽然这种情况出现的概率不大，但是依然是可能
         * 出现的，因此，当业务上确实可能出现这种情况时，我们必须多加防范。JDK也为我们考虑到了这种情况，使用
         * AtomicStampedReference可以很好的解决这个问题
         *
         */
    }
}