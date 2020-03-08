package com.juc.chat18;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * FutureTask 除了实现了Future接口，还实现了 Runnable接口，因此FutureTask可以让Executor执行，
 * 也可以交给线程执行，FutureTask表示有返回值的任务
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/27
 */
public class Demo9 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(() -> {
            System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "，start");
            TimeUnit.SECONDS.sleep(5);
            System.out.println(System.currentTimeMillis() + "，" + Thread.currentThread().getName() + "，end");
            return 10;
        });
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName());
        new Thread(futureTask).start();
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName());
        System.out.println(System.currentTimeMillis() + "," + Thread.currentThread().getName() + ",结果：" + futureTask.get());


        /**
         * 输出结果：
         * 1569587595931,main
         * 1569587595931,main
         * 1569587595931，Thread-0，start
         * 1569587600947，Thread-0，end
         * 1569587595931,main,结果：10
         *
         * 其实Demo7中的使用线程池的submit方法返回的Future实际类型就是FutureTask对象
         * FutureTask很重要
         *
         *
         *
         */
    }
}