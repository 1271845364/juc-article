package com.jvm.visualvm;

/**
 * 死锁
 * thread1持有com.jvm.visualvm.Demo4$Obj1的锁，等待获取com.jvm.visualvm.Demo4$Obj2的锁
 * thread2持有com.jvm.visualvm.Demo4$Obj2的锁，
 * 等待获取com.jvm.visualvm.Demo4$Obj1的锁，两个线程相互等待获取对方持有的锁，出现死锁。
 *
 * @author jinhui.ye@ucarinc.com
 * @date 2019/08/16
 */
public class Demo4 {

    public static void main(String[] args) {
        Obj1 obj1 = new Obj1();
        Obj2 obj2 = new Obj2();
        Thread thread1 = new Thread(new SynAddRunable(obj1, obj2, 1, 2, true));
        thread1.setName("thread1");
        thread1.start();

        Thread thread2 = new Thread(new SynAddRunable(obj1, obj2, 2, 1, false));
        thread2.setName("thread2");
        thread2.start();
    }

    public static class SynAddRunable implements Runnable {
        Obj1 obj1;
        Obj2 obj2;
        int a, b;
        boolean flag;

        public SynAddRunable(Obj1 obj1, Obj2 obj2, int a, int b, boolean flag) {
            this.obj1 = obj1;
            this.obj2 = obj2;
            this.a = a;
            this.b = b;
            this.flag = flag;
        }

        @Override
        public void run() {
            try {
                if (flag) {
                    synchronized (obj1) {
                        Thread.sleep(1000);
                        synchronized (obj2) {
                            System.out.println(a + b);
                        }
                    }
                } else {
                    synchronized (obj2) {
                        Thread.sleep(1000);
                        synchronized (obj1) {
                            System.out.println(a + b);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class Obj1 {
    }

    static class Obj2 {
    }

}