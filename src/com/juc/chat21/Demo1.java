package com.juc.chat21;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 通过反射获取Unsafe实例
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/09/30
 */
public class Demo1 {

    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(unsafe);

        /**
         * 输出结果：
         * sun.misc.Unsafe@4554617c
         *
         *
         */
    }
}