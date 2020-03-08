package com.juc.chat24;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这个就是用来操作Thread中所有口袋的东西，ThreadLocalMap源码中有一个数组（有兴趣的可以去看一下源码），对应处理者身上很多口袋一样，
 * 数组中的每个元素对应一个口袋。
 * 如何来操作Thread中的这些口袋呢，java为我们提供了一个类ThreadLocal，ThreadLocal对象用来操作Thread中的某一个口袋，
 * 可以向这个口袋中放东西、获取里面的东西、清除里面的东西，这个口袋一次性只能放一个东西，重复放东西会将里面已经存在的东西覆盖掉。
 *
 *
 * ThreadLocal 实例通常是类中的 private static 字段，它们希望将状态与某一个线程（例如，用户 ID 或事务 ID）相关联
 *
 *
 * 父线程相当于主管，子线程相当于干活的小弟，主管让小弟们干活的时候，将自己兜里面的东西复制一份给小弟们使用，
 * 主管兜里面可能有很多牛逼的工具，为了提升小弟们的工作效率，给小弟们都复制一个，丢到小弟们的兜里，
 * 然后小弟就可以从自己的兜里拿去这些东西使用了，也可以清空自己兜里面的东西。
 *
 *
 * Thread对象中有个inheritableThreadLocals变量，代码如下：
 *
 * ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
 * inheritableThreadLocals相当于线程中另外一种兜，这种兜有什么特征呢，当创建子线程的时候，
 * 子线程会将父线程这种类型兜的东西全部复制一份放到自己的inheritableThreadLocals兜中，
 * 使用InheritableThreadLocal对象可以操作线程中的inheritableThreadLocals兜。
 *
 * InheritableThreadLocal常用的方法也有3个：
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
public class Demo4 {

    /**
     * 创建一个操作Thread中存放请求任务追踪id口袋的对象，子线程可以继承父线程中内容
     */
    private static InheritableThreadLocal<String> traceIdKD = new InheritableThreadLocal<>();

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
        CountDownLatch countDownLatch = new CountDownLatch(dataList.size());

        log("执行数据库操作");
        //模拟插入数据
        for (String s : dataList) {
            new Thread(() -> {
                try {
                    //模拟数据库操作耗时100ms
                    TimeUnit.MILLISECONDS.sleep(100);
                    log("插入数据 " + s + " 成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        //等待上面dataList处理完毕
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                try {
                    controller(dataList);
                } finally {
                    //将traceId从口袋中移除
                    traceIdKD.remove();
                }
            });
        }
        disposeRequestExecutor.shutdown();

        /**
         * 输出结果：
         * ****1570712371622[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo4.controller(Demo4.java:69):接受请求
         * ****1570712371623[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo4.service(Demo4.java:74):执行业务
         * ****1570712371622[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo4.controller(Demo4.java:69):接受请求
         * ****1570712371623[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo4.service(Demo4.java:74):执行业务
         * ****1570712371623[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo4.dao(Demo4.java:81):执行数据库操作
         * ****1570712371623[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo4.controller(Demo4.java:69):接受请求
         * ****1570712371623[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo4.service(Demo4.java:74):执行业务
         * ****1570712371623[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo4.dao(Demo4.java:81):执行数据库操作
         * ****1570712371623[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo4.dao(Demo4.java:81):执行数据库操作
         * ****1570712371725[traceId:1]，[线程：Thread-5]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据1 成功
         * ****1570712371725[traceId:1]，[线程：Thread-6]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据2 成功
         * ****1570712371725[traceId:1]，[线程：Thread-3]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据0 成功
         * ****1570712371725[traceId:0]，[线程：Thread-7]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据0 成功
         * ****1570712371725[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo4.controller(Demo4.java:69):接受请求
         * ****1570712371725[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo4.service(Demo4.java:74):执行业务
         * ****1570712371725[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo4.dao(Demo4.java:81):执行数据库操作
         * ****1570712371725[traceId:2]，[线程：Thread-4]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据0 成功
         * ****1570712371725[traceId:0]，[线程：Thread-8]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据1 成功
         * ****1570712371725[traceId:0]，[线程：Thread-9]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据2 成功
         * ****1570712371726[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo4.controller(Demo4.java:69):接受请求
         * ****1570712371726[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo4.service(Demo4.java:74):执行业务
         * ****1570712371726[traceId:2]，[线程：Thread-10]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据1 成功
         * ****1570712371726[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo4.dao(Demo4.java:81):执行数据库操作
         * ****1570712371726[traceId:2]，[线程：Thread-11]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据2 成功
         * ****1570712371827[traceId:3]，[线程：Thread-14]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据2 成功
         * ****1570712371827[traceId:3]，[线程：Thread-13]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据1 成功
         * ****1570712371827[traceId:3]，[线程：Thread-12]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据0 成功
         * ****1570712371827[traceId:4]，[线程：Thread-15]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据0 成功
         * ****1570712371827[traceId:4]，[线程：Thread-17]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据2 成功
         * ****1570712371827[traceId:4]，[线程：Thread-16]，com.juc.chat24.Demo4.lambda$dao$1(Demo4.java:88):插入数据 数据1 成功
         *
         * 输出中共有traceId，和期望的结果一样
         *
         *
         *
         */
    }
}