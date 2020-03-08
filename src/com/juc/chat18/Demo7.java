package com.juc.chat18;

import java.util.concurrent.*;

/**
 * Future cancel(boolean mayInterruptIfRunning);
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/27
 */
public class Demo7 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Integer> future = executorService.submit(() -> {
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",start");
            TimeUnit.SECONDS.sleep(5);
            System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",end");
            return 10;
        });

        executorService.shutdown();

        TimeUnit.SECONDS.sleep(1);
        future.cancel(false);
        System.out.println(future.isCancelled());
        System.out.println(future.isDone());


        TimeUnit.SECONDS.sleep(5);
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName());
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",结果：" + future.get());
        executorService.shutdown();


        /**
         * 输出结果：
         * 1569586205576,pool-1-thread-1,start
         * true
         * true
         * 1569586210591,pool-1-thread-1,end
         * 1569586211601,main
         * Exception in thread "main" java.util.concurrent.CancellationException
         * 	at java.util.concurrent.FutureTask.report(FutureTask.java:121)
         * 	at java.util.concurrent.FutureTask.get(FutureTask.java:192)
         * 	at com.juc.chat18.Demo7.main(Demo7.java:32)
         *
         *
         * 	输出的两个true，表示已经取消，已经完成，最后调用get方法会触发CancellationException 异常
         * 	可以得出，Future、Callable接口需要结合ExecutorService来使用，需要有线程池的支持
         *
         */
    }

}