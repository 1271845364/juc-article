package com.juc.chat18;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 异步执行一批任务，有一个完成立即返回，其他取消
 * 使用 ExecutorCompletionService 实现，ExecutorCompletionService 提供了获取一批任务中最先完成的任务结果的能力
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/29
 */
public class Demo16 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<Integer>> list = new ArrayList<>();
        int taskCount = 5;
        for (int i = taskCount; i > 0; i--) {
            int j = 2 * i;
            String taskName = "任务" + i;
            list.add(() -> {
                TimeUnit.SECONDS.sleep(j);
                System.out.println(taskName + "执行完毕");
                return j;
            });
        }
        Integer integer = invokeAny(executorService, list);
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime) + "ms，执行结果：" + integer);
        executorService.shutdown();

        /**
         * 输出结果：
         * 任务1执行完毕
         * 耗时：2127ms，执行结果：2
         *
         * 代码中执行了5个任务，使用 CompletionService 执行任务，调用take方法获取最先执行的完成的任务，然后返回。
         * 在finally中对所有任务发送取消操作(future.cacel(true))，从输出中可以看出只有任务1执行成功，其他任务
         * 被成功取消了，符合预期结果
         *
         */
    }

    public static <T> T invokeAny(Executor e, Collection<Callable<T>> collection) {
        CompletionService<T> completionService = new ExecutorCompletionService<>(e);
        List<Future<T>> list = new ArrayList<>();
        for (Callable<T> tCallable : collection) {
            list.add(completionService.submit(tCallable));
        }

        int num = collection.size();
        try {
            for (int i = 0; i < num; i++) {
                T t = completionService.take().get();
                if (t != null) {
                    return t;
                }
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        } finally {
            for (Future<T> tFuture : list) {
                tFuture.cancel(true);
            }
        }
        return null;
    }
}