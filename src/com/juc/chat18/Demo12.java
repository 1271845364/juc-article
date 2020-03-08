package com.juc.chat18;

import java.util.concurrent.*;

/**
 * 买新房了，然后在网上下单买冰箱、洗衣机，电器商家不同，所以送货耗时不一样，然后等他们送货，快递只愿送到楼下，然后我们自己将其搬到楼上的家中。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/27
 */
public class Demo12 {

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

        //异步购买冰箱
        Future<GoodsModel> bxFuture = executorService.submit(buyGoods("冰箱", 5));

        //异步购买洗衣机
        Future<GoodsModel> xyjFuture = executorService.submit(buyGoods("洗衣机", 2));

        //关闭线程池
        executorService.shutdown();

        //等待冰箱送到
        GoodsModel bxGoodsModel = bxFuture.get();

        //将冰箱搬上楼
        moveUp(bxGoodsModel);

        //等待洗衣机送到
        GoodsModel xyjGoodsModel = xyjFuture.get();

        //将洗衣机搬上楼
        moveUp(xyjGoodsModel);

        long et = System.currentTimeMillis();
        System.out.println(et + "货物都送到家了，哈哈哈");
        System.out.println("总耗时：" + (et - st) + "ms");

        /**
         * 输出结果：
         * 1569589695554开始购物
         * 1569589695658购买冰箱下单！
         * 1569589695658购买洗衣机下单！
         * 1569589697658洗衣机送到了
         * 1569589700658冰箱送到了
         * 将商品搬上楼，商品信息：GoodsModel{name='冰箱', startTime=1569589695658, endTime=1569589700658}
         * 将商品搬上楼，商品信息：GoodsModel{name='洗衣机', startTime=1569589695658, endTime=1569589697658}
         * 1569589710658货物都送到家了，哈哈哈
         * 总耗时：15104ms
         *
         * 1、购买冰箱5s
         * 2、购买洗衣机2s
         * 3、将冰箱送上楼5s
         * 4、将洗衣机送上楼5s
         * 5、共计耗时15s
         *
         * 购买冰箱和洗衣机是异步执行的，先把冰箱送上楼，再把洗衣机送上楼。实际上是洗衣机先到的，冰箱后到的，并没有先把先到的洗衣机送上楼
         * 而是等待冰箱bxFuture.get()到货，然后将冰箱送上楼，中间浪费了3s，现实生活应该是洗衣机到了，先把洗衣机送到楼上
         */
    }
}