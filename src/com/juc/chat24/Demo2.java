package com.juc.chat24;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 目前java开发web系统一般有3层，controller、service、dao，请求到达controller，controller调用service，service调用dao，然后进行处理。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/08
 */
public class Demo2 {

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
    public static void log(String msg, String traceId) {
        StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
        System.out.println("****" + System.currentTimeMillis() + "[traceId:" + traceId + "]，[线程：" +
                Thread.currentThread().getName() + "]，" + stackTrace[1] + ":" + msg);
    }

    public static void controller(List<String> dataList, String traceId) {
        log("接受请求", traceId);
        service(dataList, traceId);
    }

    public static void service(List<String> dataList, String traceId) {
        log("执行业务", traceId);
        dao(dataList, traceId);
    }

    public static void dao(List<String> dataList, String traceId) {
        log("执行数据库操作", traceId);
        for (String s : dataList) {
            log("插入数据 " + s + " 成功", traceId);
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
                controller(dataList, traceId);
            });
        }
        disposeRequestExecutor.shutdown();

        /**
         * 输出结果：
         * ****1570540337888[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.controller(Demo2.java:43):接受请求
         * ****1570540337888[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.service(Demo2.java:48):执行业务
         * ****1570540337888[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:53):执行数据库操作
         * ****1570540337888[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.controller(Demo2.java:43):接受请求
         * ****1570540337888[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据0 成功
         * ****1570540337888[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.service(Demo2.java:48):执行业务
         * ****1570540337888[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据1 成功
         * ****1570540337888[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:53):执行数据库操作
         * ****1570540337888[traceId:1]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据2 成功
         * ****1570540337888[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据0 成功
         * ****1570540337888[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据1 成功
         * ****1570540337888[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.controller(Demo2.java:43):接受请求
         * ****1570540337888[traceId:0]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据2 成功
         * ****1570540337888[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.service(Demo2.java:48):执行业务
         * ****1570540337888[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.controller(Demo2.java:43):接受请求
         * ****1570540337888[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo2.controller(Demo2.java:43):接受请求
         * ****1570540337888[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.service(Demo2.java:48):执行业务
         * ****1570540337888[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:53):执行数据库操作
         * ****1570540337888[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:53):执行数据库操作
         * ****1570540337888[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo2.service(Demo2.java:48):执行业务
         * ****1570540337889[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据0 成功
         * ****1570540337888[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据0 成功
         * ****1570540337889[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据1 成功
         * ****1570540337889[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo2.dao(Demo2.java:53):执行数据库操作
         * ****1570540337889[traceId:4]，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据2 成功
         * ****1570540337889[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据1 成功
         * ****1570540337889[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据0 成功
         * ****1570540337889[traceId:3]，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据2 成功
         * ****1570540337889[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据1 成功
         * ****1570540337889[traceId:2]，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo2.dao(Demo2.java:55):插入数据 数据2 成功
         *
         *
         *
         */
    }
}