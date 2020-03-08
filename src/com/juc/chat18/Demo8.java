package com.juc.chat18;

import java.util.concurrent.*;

/**
 * 超时获取异步任务执行结果
 * 可能任务比较耗时，比如耗时1分钟，我最多只能等待10s，如果10s没返回，我就做其他事情了
 * V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/27
 */
public class Demo8 {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Integer> future = executorService.submit(() -> {
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",start");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",end");
            return 10;
        });
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName());
        try {
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",结果：" + future.get(3, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        /**
         * 输出结果：
         * 1569584659649,main
         * 1569584659649,pool-1-thread-1,start
         * java.util.concurrent.TimeoutException
         * 	at java.util.concurrent.FutureTask.get(FutureTask.java:205)
         * 	at com.juc.chat18.Demo8.main(Demo8.java:32)
         * 1569584664664,pool-1-thread-1,end
         *
         * 任务执行中休眠了5s，get方法获取执行结果，超时时间是3s，3s还未获取到结果，
         * get触发了TimeoutException异常，当前线程从阻塞状态苏醒了
         *
         */
    }

}