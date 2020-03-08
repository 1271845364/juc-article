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
public class Demo1 {

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
        System.out.println("****" + System.currentTimeMillis() + "，[线程：" +
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
            disposeRequestExecutor.execute(() -> {
                controller(dataList);
            });
        }
        disposeRequestExecutor.shutdown();

        /**
         * 输出结果：
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.controller(Demo1.java:43):接受请求
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.service(Demo1.java:48):执行业务
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:53):执行数据库操作
         * ****1570540035815，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.controller(Demo1.java:43):接受请求
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据0 成功
         * ****1570540035815，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.controller(Demo1.java:43):接受请求
         * ****1570540035815，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.service(Demo1.java:48):执行业务
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据1 成功
         * ****1570540035815，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.service(Demo1.java:48):执行业务
         * ****1570540035815，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:53):执行数据库操作
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据2 成功
         * ****1570540035815，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据0 成功
         * ****1570540035815，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据1 成功
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.controller(Demo1.java:43):接受请求
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.service(Demo1.java:48):执行业务
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:53):执行数据库操作
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据0 成功
         * ****1570540035815，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据2 成功
         * ****1570540035815，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:53):执行数据库操作
         * ****1570540035816，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据0 成功
         * ****1570540035816，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据1 成功
         * ****1570540035816，[线程：disposeRequestRequest-1]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据2 成功
         * ****1570540035815，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据1 成功
         * ****1570540035816，[线程：disposeRequestRequest-2]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据2 成功
         * ****1570540035816，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.controller(Demo1.java:43):接受请求
         * ****1570540035816，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.service(Demo1.java:48):执行业务
         * ****1570540035816，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:53):执行数据库操作
         * ****1570540035816，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据0 成功
         * ****1570540035816，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据1 成功
         * ****1570540035816，[线程：disposeRequestRequest-3]，com.juc.chat24.Demo1.dao(Demo1.java:55):插入数据 数据2 成功
         *
         * 开发者想看一下哪些地方耗时比较多，想通过日志来分析耗时情况，想追踪某个请求的完整日志，怎么搞？
         * 上面的请求采用线程池的方式处理的，多个请求可能会被一个线程处理，通过日志很难看出那些日志是同一个请求，
         * 我们能不能给请求加一个唯一标志，日志中输出这个唯一标志，当然可以。
         * 如果我们的代码就只有上面示例这么简单，我想还是很容易的，上面就3个方法，给每个方法加个traceId参数，log方法也加个traceId参数，就解决了
         *
         *
         */
    }
}