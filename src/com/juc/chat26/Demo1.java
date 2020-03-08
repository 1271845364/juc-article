package com.juc.chat26;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 需求详见README.md
 * <p>
 * 伪代码如下：
 * public Map<String,Object> detail(long goodsId){
 * //创建一个map
 * //step1：查询商品基本信息，放入map
 * map.put("goodsModel",(select * from t_goods where id = #gooldsId#));
 * //step2：查询商品图片列表，返回一个集合放入map
 * map.put("goodsImgsModelList",(select * from t_goods_imgs where goods_id = #gooldsId#));
 * //step3：查询商品描述信息，放入map
 * map.put("goodsExtModel",(select * from t_goods_ext where goods_id = #gooldsId#));
 * return map;
 * }
 * 上面这种写法应该很常见，代码很简单，假设上面每个步骤耗时200ms，此接口总共耗时>=600毫秒，其他还涉及到网络传输耗时，
 * 估计总共会在700ms左右，此接口有没有优化的空间，性能能够提升多少？我们一起来挑战一下。
 * <p>
 * 在看一下上面的逻辑，整个过程是按顺序执行的，实际上3个查询之间是没有任何依赖关系，
 * 所以说3个查询可以同时执行，那我们对这3个步骤采用多线程并行执行，看一下最后什么情况，代码如下：
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/15
 */
public class Demo1 {

    /**
     * 获取商品基本信息
     *
     * @param goodsId 商品id
     * @return 商品基本信息
     * @throws InterruptedException
     */
    public String goodsDetailModel(long goodsId) throws InterruptedException {
        //模拟耗时，休眠200ms
        TimeUnit.MILLISECONDS.sleep(200);
        return "商品id：" + goodsId + "，商品基本信息...";
    }

    /**
     * 获取商品图片列表
     *
     * @param goodsId 商品id
     * @return 商品图片列表
     * @throws InterruptedException
     */
    public List<String> goodsImgsModelList(long goodsId) throws InterruptedException {
        //模拟耗时，休眠200ms
        TimeUnit.MILLISECONDS.sleep(200);
        return Arrays.asList("图1", "图2", "图3");
    }

    /**
     * 获取商品基本信息
     *
     * @param goodsId 商品id
     * @return 商品基本信息
     * @throws InterruptedException
     */
    public String goodsExtModel(long goodsId) throws InterruptedException {
        //模拟耗时，休眠200ms
        TimeUnit.MILLISECONDS.sleep(200);
        return "商品id：" + goodsId + "，商品描述信息...";
    }

    /**
     * 创建一个固定的线程池
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        Map<String, Object> map = new Demo1().goodsDetail(1L);
        System.out.println(map);
        System.out.println("耗时(ms):" + (System.currentTimeMillis() - startTime));

        /**
         * 输出结果：
         * {goodsDetailModel=商品id：1，商品基本信息..., goodsExtModel=商品id：1，商品描述信息..., goodImgsModelList=[图1, 图2, 图3]}
         * 耗时(ms):297
         *
         * 耗时200多ms，性能提升了2倍，假如这个接口中还存在其他的无依赖的操作，性能提升将更加显著，上面使用了线程池并行去执行3次查询的任务，
         * 最后通过Future获取异步执行结果。
         *
         * 优化过程：
         * 1、找到没有依赖的操作
         * 2、将这些操作修改为并行的
         *
         * 用到的技术：
         * 1、线程池
         * 2、Executors、Future
         *
         * 总结：
         * 1、对于无依赖的操作尽量采用并行方式去执行，可以很好的提升接口的性能
         */
    }

    /**
     * 获取商品详情
     *
     * @param goodsId 商品id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private Map<String, Object> goodsDetail(long goodsId) throws ExecutionException, InterruptedException {
        Map<String, Object> result = new HashMap<>();

        //异步获取商品基本信息
        Future<String> goodsDetailModelFuture = executorService.submit(() -> this.goodsDetailModel(goodsId));
        //异步获取商品图片列表
        Future<List<String>> goodsImgsModelListFuture = executorService.submit(() -> this.goodsImgsModelList(goodsId));
        //异步获取商品描述信息
        Future<String> goodExtModelFuture = executorService.submit(() -> this.goodsExtModel(goodsId));

        result.put("goodsDetailModel", goodsDetailModelFuture.get());
        result.put("goodImgsModelList", goodsImgsModelListFuture.get());
        result.put("goodsExtModel", goodExtModelFuture.get());
        return result;
    }

}