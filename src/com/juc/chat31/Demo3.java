package com.juc.chat31;

import java.util.concurrent.*;

/**
 * 3、ExecutorService.submit实现获取线程结果
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/16
 */
public class Demo3 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println(System.currentTimeMillis());
        //创建一个线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Integer> future = executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 3;
        });


        Integer rs = future.get();
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() + ":" + rs);

        /**
         * 输出结果：
         * 1571231259467
         * 1571231262526
         * 1571231262526:3
         */
    }
}