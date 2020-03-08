package com.juc.chat18;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 异步执行一批任务，有一个完成立即返回，其他取消
 * 其实 ExecutorService 提供了这样的方法
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/29
 */
public class Demo17 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
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
        Integer integer = executorService.invokeAny(list);
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime) + "ms，执行结果：" + integer);
        executorService.shutdown();

        /**
         * 输出结果：
         * 任务1执行完毕
         * 耗时：2107ms，执行结果：2
         *
         */
    }

}