package com.juc.chat18;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 执行一批任务，然后消费执行结果
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/29
 */
public class Demo15 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<Integer>> list = new ArrayList<>();
        int taskCount = 5;
        for (int i = taskCount; i > 0; i--) {
            int j = i * 2;
            list.add(() -> {
                TimeUnit.SECONDS.sleep(j);
                return j;
            });
        }

        solve(executorService, list,a->{
            System.out.println(System.currentTimeMillis() + ":" + a);
        });

        executorService.shutdown();

        /**
         * 输出结果：
         * 1569746893735:2
         * 1569746895736:4
         * 1569746897736:6
         * 1569746899736:8
         * 1569746901736:10
         *
         * 代码中传入了一批任务进行处理，最终将所有处理完成的按任务的先后顺序返回给Consumer进行消费了
         *
         */
    }

    private static <T> void solve(Executor executor, Collection<Callable<T>> collection, Consumer<T> consumer) throws InterruptedException, ExecutionException {
        CompletionService<T> completionService = new ExecutorCompletionService<T>(executor);
        for (Callable<T> tCallable : collection) {
            completionService.submit(tCallable);
        }

        int n = collection.size();
        for (int i = 0; i < n; i++) {
            T t = completionService.take().get();
            if(t != null) {
                consumer.accept(t);
            }
        }
    }

}