package com.juc.chat24;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 这个就是用来操作Thread中所有口袋的东西，ThreadLocalMap源码中有一个数组（有兴趣的可以去看一下源码），对应处理者身上很多口袋一样，
 * 数组中的每个元素对应一个口袋。
 * 如何来操作Thread中的这些口袋呢，java为我们提供了一个类ThreadLocal，ThreadLocal对象用来操作Thread中的某一个口袋，
 * 可以向这个口袋中放东西、获取里面的东西、清除里面的东西，这个口袋一次性只能放一个东西，重复放东西会将里面已经存在的东西覆盖掉。
 *
 * ThreadLocal 实例通常是类中的 private static 字段，它们希望将状态与某一个线程（例如，用户 ID 或事务 ID）相关联
 *
 *
 * //向Thread中某个口袋中放东西
 * public void set(T value);
 * //获取这个口袋中目前放的东西
 * public T get();
 * //清空这个口袋中放的东西
 * public void remove()
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/10
 */
public class Demo3 {

    /**
     * 创建一个操作Thread中存放请求任务追踪id口袋的对象
     */
    private static ThreadLocal<String> traceIdKD = new ThreadLocal<>();

    public static AtomicInteger threadIndex = new AtomicInteger(1);

    /**
     * 处理业务的线程池
     */
    public static ThreadPoolExecutor disposeRequestExecutor = new ThreadPoolExecutor(3,
            3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("disposeRequestRequest-" + threadIndex.getAndIncrement());
                return thread;
            });

    /**
     * 打印日志
     *
     * @param msg
     */
    public static void log(String msg) {
        StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
        String traceId = traceIdKD.get();
        System.out.println("****" + System.currentTimeMillis() + "[traceId:" + traceId + "]，[线程：" +
                Thread.currentThread().getName() + "]，" + stackTrace[1] + ":" + msg);
    }

    public static void controller(List<String> dataList) {
        log("接受请求");
        service(dataList);
    }

    public static void service(List<String> dataList) {
        log("执行业务");
        dao(dataList);
    }

    public static void dao(List<String> dataList) {
        log("执行数据库操作");
        for (String s : dataList) {
            log("插入数据 " + s + " 成功");
        }
    }


    public static void main(String[] args) {
        //需要插入的数据
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dataList.add("数据" + i);
        }

        //模拟5个请求
        int requestCount = 5;
        for (int i = 0; i < requestCount; i++) {
            String traceId = String.valueOf(i);
            disposeRequestExecutor.execute(() -> {
                //将traceId放入到口袋中
                traceIdKD.set(traceId);
                try{
                    controller(dataList);
                }finally {
                    //将traceId从口袋中移除
                    traceIdKD.remove();
                }
            });
        }
        disposeRequestExecutor.shutdown();

        /**
         * 输出结果：
         * ****1570706486508[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.controller(Demo1.java:60):接受请求
         * ****1570706486508[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.controller(Demo1.java:60):接受请求
         * ****1570706486508[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.service(Demo1.java:65):执行业务
         * ****1570706486508[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.controller(Demo1.java:60):接受请求
         * ****1570706486508[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.service(Demo1.java:65):执行业务
         * ****1570706486508[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:70):执行数据库操作
         * ****1570706486508[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:70):执行数据库操作
         * ****1570706486509[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据0 成功
         * ****1570706486509[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据1 成功
         * ****1570706486509[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据2 成功
         * ****1570706486509[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据0 成功
         * ****1570706486509[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据1 成功
         * ****1570706486509[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据2 成功
         * ****1570706486508[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.service(Demo1.java:65):执行业务
         * ****1570706486509[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:70):执行数据库操作
         * ****1570706486509[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据0 成功
         * ****1570706486509[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据1 成功
         * ****1570706486509[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据2 成功
         * ****1570706486509[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.controller(Demo1.java:60):接受请求
         * ****1570706486509[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.service(Demo1.java:65):执行业务
         * ****1570706486509[traceId:3]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.controller(Demo1.java:60):接受请求
         * ****1570706486509[traceId:3]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.service(Demo1.java:65):执行业务
         * ****1570706486509[traceId:3]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:70):执行数据库操作
         * ****1570706486509[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:70):执行数据库操作
         * ****1570706486509[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据0 成功
         * ****1570706486509[traceId:3]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据0 成功
         * ****1570706486510[traceId:3]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据1 成功
         * ****1570706486510[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据1 成功
         * ****1570706486510[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据2 成功
         * ****1570706486510[traceId:3]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:72):插入数据 数据2 成功
         *
         * 输出结果和Demo2一样，但是却简单了很多。不用去修改controller、service、dao的代码，风险也减少了很多
         * 创建了一个traceIdKD用来当做thread的一个口袋，只能放一个东西，放traceId。
         * 在main中通过traceIdKD.set(traceId)；将traceId放到口袋中，log方法中通过traceIdKD.get();获取口袋中的traceId
         * 最后任务处理完，将traceId从traceIdKD中移除
         *
         *
         */
    }
}