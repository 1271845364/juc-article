package com.juc.chat31;

import java.util.concurrent.TimeUnit;

/**
 * 1、join实现获取线程结果
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/16
 */
public class Demo1 {

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
        Result<Integer> result = new Result<>();
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result.setT(3);
        });

        thread.start();
        new Thread(()->{
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("a" + result.getT());
        }).start();
        new Thread(()->{
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("b" + result.getT());
        }).start();
        thread.join();


        Integer rs = result.getT();
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() + ":" + rs);

        /**
         * 输出结果：
         * 1571231092902
         * 1571231095962
         * 1571231095962:3
         */
    }
}