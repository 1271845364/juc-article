package com.juc.chat31;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 2、CountDownLatch实现获取线程结果
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/16
 */
public class Demo2 {

    /**
     * 封装结果
     */
    static class Result<T> {
        private T t;

        public void setT(T t) {
            this.t = t;
        }

        public T getT() {
            return t;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.currentTimeMillis());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Result<Integer> result = new Result<>();
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result.setT(3);
            countDownLatch.countDown();
        });

        thread.start();
        countDownLatch.await();

        Integer rs = result.getT();
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() + ":" + rs);

        /**
         * 输出结果：
         * 1571231071394
         * 1571231074445
         * 1571231074445:3
         */
    }
}