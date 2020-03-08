package com.juc.chat31;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 5、FutureTask2实现获取线程结果
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/16
 */
public class Demo5 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println(System.currentTimeMillis());

        FutureTask<Integer> futureTask = new FutureTask<>(() -> 10);

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            futureTask.run();
        }).start();

        Integer rs = futureTask.get();
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() + ":" + rs);

        /**
         * 输出结果：
         * 1571231746522
         * 1571231749571
         * 1571231749571:10
         *
         */
    }
}