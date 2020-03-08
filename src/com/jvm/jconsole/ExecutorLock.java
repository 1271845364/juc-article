package com.jvm.jconsole;

import java.util.concurrent.*;

/**
 * 饥饿死锁的例子
 *
 * 堆栈信息结合图中的代码，可以看出主线程在38行处于等待中，线程池中的工作线程在31行处于等待中，等待获取结果。
 * 由于线程池是一个线程，AnotherCallable得不到执行，而被饿死，最终导致了程序死锁的现象。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/16
 */
public class ExecutorLock {
    private static ExecutorService single = Executors.newSingleThreadExecutor();

    public static class AnotherCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println("in AnotherCallable");
            return "annother success";
        }
    }

    public static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("in MyCallable");
            Future<String> submit = single.submit(new AnotherCallable());
            return submit.get();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyCallable myCallable = new MyCallable();
        Future<String> submit = single.submit(myCallable);
        System.out.println(submit.get());
        System.out.println("over");
    }

}