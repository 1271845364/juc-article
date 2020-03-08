package com.juc.chat18;

import java.util.concurrent.*;

/**
 * 获取异步任务执行结果
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/27
 */
public class Demo6 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
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
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",结果：" + future.get());

        /**
         * 输出结果：
         * 1569583013104,main
         * 1569583013104,pool-1-thread-1,start
         * 1569583018135,pool-1-thread-1,end
         * 1569583013104,main,结果：10
         *
         * 调用线程池的submit方法执行任务，submit方法的参数为Callable 表示需要执行有返回值的任务，submit的方法返回一个Future对象，
         * Future相当于一个凭证，可以在任意时间拿着这个凭证去获取这个任务的执行结果(调用get方法)，代码中调用了result.get()方法之后，
         * 此方法会阻塞当前线程直到任务执行结束
         *
         *
         *
         */
    }
}