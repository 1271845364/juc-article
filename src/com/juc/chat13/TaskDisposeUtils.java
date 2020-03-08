package com.juc.chat13;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 并行处理任务工具类
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/17
 */
public class TaskDisposeUtils {

    /**
     * 并行线程数
     */
    private static final int POOL_SIZE;

    static {
        POOL_SIZE = Integer.max(Runtime.getRuntime().availableProcessors(), 5);
    }

    /**
     * 并行处理，并等待结束
     *
     * @param list     任务列表
     * @param consumer 消费者
     * @param <T>
     */
    public static <T> void dispose(List<T> list, Consumer<T> consumer) {
        dispose(true, POOL_SIZE, list, consumer);
    }

    /**
     * 并行处理，并等待结束
     *
     * @param moreThread 是否多线程处理
     * @param poolSize   并行处理，线程池个数
     * @param list       任务列表
     * @param consumer   消费者
     * @param <T>
     */
    public static <T> void dispose(boolean moreThread, int poolSize, List<T> list, Consumer<T> consumer) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (moreThread && poolSize > 1) {
            //多线程处理
            ExecutorService executorService = null;
            try {
                poolSize = Math.max(poolSize, list.size());
                executorService = Executors.newFixedThreadPool(poolSize);
                CountDownLatch countDownLatch = new CountDownLatch(poolSize);
                for (T t : list) {
                    executorService.execute(() -> {
                        try {
                            consumer.accept(t);
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
                }
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (executorService != null) {
                    executorService.shutdown();
                }
            }
        } else {
            //单线程处理=串行处理
            for (T t : list) {
                consumer.accept(t);
            }
        }
    }

    public static void main(String[] args) {
        //生产1-10的10个数字
        List<Integer> list = Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList());
        TaskDisposeUtils.dispose(list, item -> {
            try {
                long startTime = System.currentTimeMillis();
                TimeUnit.SECONDS.sleep(item);
                long endTime = System.currentTimeMillis();
                System.out.println(System.currentTimeMillis() + ",任务 " + item + " 执行完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(list + " 中的任务处理完毕");

        /**
         * 输出结果：
         * 1568722302321,任务 1 执行完毕
         * 1568722303321,任务 2 执行完毕
         * 1568722304321,任务 3 执行完毕
         * 1568722305321,任务 4 执行完毕
         * 1568722306321,任务 5 执行完毕
         * 1568722307321,任务 6 执行完毕
         * 1568722308321,任务 7 执行完毕
         * 1568722309321,任务 8 执行完毕
         * 1568722310321,任务 9 执行完毕
         * 1568722311321,任务 10 执行完毕
         * [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] 中的任务处理完毕
         *
         * 是一个并行处理的工具类，可以传入n个任务内部使用线程池并行处理，每个任务都没有返回值，等待所有任务处理完成之后，
         * 方法才会返回。比如我们发送短信，系统中发送1w条短信，每次取100条并行发送，待100个都处理完毕之后，再取下一批
         * 按照同样的逻辑发送
         *
         */
    }
}