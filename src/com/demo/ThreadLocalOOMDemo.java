package com.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadLocal内存溢出
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/11/27
 */
public class ThreadLocalOOMDemo {

    private static final int THREAD_LOOP_SIZE = 500;
    private static final int MOCK_BIG_DATA_LOOP_SIZE = 10000;
    private static ThreadLocal<List<User>> threadLocal = new ThreadLocal<List<User>>();

    public static void main(String[] args) {
        //定义指定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_LOOP_SIZE);
        for (int i = 0; i < THREAD_LOOP_SIZE; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    threadLocal.set(new ThreadLocalOOMDemo().addBigList());
                    Thread thread = Thread.currentThread();
                    System.out.println(thread.getName());
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private List<User> addBigList() {
        List<User> params = new ArrayList<>();
        for (int i = 0; i < MOCK_BIG_DATA_LOOP_SIZE; i++) {
            params.add(new User("zhangsan", "password" + i, "难", i));
        }
        return params;
    }

}

class User {
    private String name;
    private String pwd;
    private String gender;
    private int age;

    public User(String name, String pwd, String gender, int age) {
        this.name = name;
        this.pwd = pwd;
        this.gender = gender;
        this.age = age;
    }
}