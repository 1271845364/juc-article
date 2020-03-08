package com.juc.chat31;

import java.util.concurrent.*;

/**
 * 4、FutureTask1实现获取线程结果
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/16
 */
public class Demo4 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println(System.currentTimeMillis());

        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
            }
            return 10;
        });

        new Thread(futureTask).start();

        Integer rs = futureTask.get();
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() + ":" + rs);

        /**
         * 输出结果：
         * 1571231617856
         * 1571231620907
         * 1571231620907:10
         *
         */
    }
}