package com.juc.chat18;

import java.util.concurrent.*;

/**
 * 买新房了，然后在网上下单买冰箱、洗衣机，电器商家不同，所以送货耗时不一样，然后等他们送货，快递只愿送到楼下，然后我们自己将其搬到楼上的家中。
 * <p>
 * <p>
 * CompletionService 接口相当于一个执行任务的服务，通过submit丢任务给这个服务，服务内部去执行任务，可以通过
 * 提供的一些方法获取服务中的已经完成的任务
 * <p>
 * 用于向服务中提交有返回结果的任务，并返回Future对象
 * Future<V> submit(Callable<V> task);
 * <p>
 * 用于向服务中提交有返回结果的任务，并返回Future对象
 * Future<V> submit(Runnable task, V result);
 * <p>
 * 从服务中返回并移除一个已经完成的任务，如果获取不到，会一直阻塞直到有返回值为止。此方法会响应线程中断
 * Future<V> take() throws InterruptedException;
 * <p>
 * 从服务中返回并移除一个已经完成的任务，如果内部没有已经完成的任务，则返回空，此方法会立即响应
 * Future<V> poll();
 * <p>
 * 尝试在指定时间内从服务中返回并移除一个已经完成的任务，等待的时间超时还是没有获取到已完成的任务，则返回空。此方法会响应线程中断
 * Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
 * <p>
 * ExecutorCompletionService 类是CompletionService接口的具体实现。
 * 内部原理，创建的时候需要传入一个线程池，调用submit方法需要传入Runnable task，任务由内部线程池来处理；ExecutorCompletionService
 * 内部有个阻塞队列，任意一个任务完成之后，会将任务的执行结果(Future)放入阻塞队列中，然后其他线程可以调用task、poll方法从这个阻塞队列中
 * 获取一个已经完成的任务，获取任务返回结果的顺序和任务执行完成的先后顺序一致，所以最先完成的任务会先返回
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/29
 */
public class Demo14 {

    static class GoodsModel {
        /**
         * 商品名称
         */
        String name;

        /**
         * 购物开始时间
         */
        long startTime;

        /**
         * 送到的时间
         */
        long endTime;

        public GoodsModel(String name, long startTime, long endTime) {
            this.name = name;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String toString() {
            return "GoodsModel{" +
                    "name='" + name + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    '}';
        }
    }

    /**
     * 将商品搬上楼
     *
     * @param goodsModel
     * @throws InterruptedException
     */
    static void moveUp(GoodsModel goodsModel) throws InterruptedException {
        //模拟搬上楼耗时
        TimeUnit.SECONDS.sleep(5);
        System.out.println("将商品搬上楼，商品信息：" + goodsModel);
    }

    /**
     * 购买商品
     *
     * @param name     商品名称
     * @param costTime 耗时
     * @return
     */
    static Callable<GoodsModel> buyGoods(String name, long costTime) {
        return () -> {
            long startTime = System.currentTimeMillis();
            System.out.println(startTime + "购买" + name + "下单！");
            //模拟送货耗时
            TimeUnit.SECONDS.sleep(costTime);
            long endTime = System.currentTimeMillis();
            System.out.println(endTime + name + "送到了");
            return new GoodsModel(name, startTime, endTime);
        };
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long st = System.currentTimeMillis();
        System.out.println(st + "开始购物");

        //创建一个线程池，异步下单
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        //创建ExecutorCompletionService对象
        ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executorService);

        //异步购买冰箱
        executorCompletionService.submit(buyGoods("冰箱", 5));

        //异步购买洗衣机
        executorCompletionService.submit(buyGoods("洗衣机", 2));

        //关闭线程池
        executorService.shutdown();

        //购买的商品数量
        int goodsCount = 2;
        for (int i = 0; i < goodsCount; i++) {
            //获取到最先到的商品
            GoodsModel goodsModel = (GoodsModel) executorCompletionService.take().get();
            moveUp(goodsModel);
        }

        long et = System.currentTimeMillis();
        System.out.println(et + "货物都送到家了，哈哈哈");
        System.out.println("总耗时：" + (et - st) + "ms");

        /**
         * 输出结果：
         * 1569745044025开始购物
         * 1569745044139购买冰箱下单！
         * 1569745044139购买洗衣机下单！
         * 1569745046140洗衣机送到了
         * 1569745049140冰箱送到了
         * 将商品搬上楼，商品信息：GoodsModel{name='洗衣机', startTime=1569745044139, endTime=1569745046140}
         * 将商品搬上楼，商品信息：GoodsModel{name='冰箱', startTime=1569745044139, endTime=1569745049140}
         * 1569745056142货物都送到家了，哈哈哈
         * 总耗时：12117ms
         *
         * 从输出结果中可以看出和我们希望的结果一致，代码中下单顺序是：冰箱、洗衣机，冰箱送货耗时5s，洗衣机送货耗时2s，洗衣机先到的，
         * 然后被送上楼了，冰箱后到的被送上楼，总共耗时12s，和预期的结果一样
         *
         *
         *
         */
    }
}