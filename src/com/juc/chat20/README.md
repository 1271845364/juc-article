 第21天：java中的CAS，你需要知道的东西
 
 1. 从网站计数器实现中一步步引出CAS操作
 2. 介绍java中的CAS及CAS可能存在的问题
 3. 悲观锁和乐观锁的一些介绍及数据库乐观锁的一个常见示例
 4. 使用java中的原子操作实现网站计数器功能

java中提供了对CAS操作的支持，具体在sun.misc.Unsafe类中，声明如下：

public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);

public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);

public final native boolean compareAndSwapLong(Object var1, long var2, long var4, long var6);

上面的三个方法是类似的，4个参数说明：

var1：表示要操作的对象

var2：表示要操作对象中属性地址的偏移量

var3：表示需要修改数据的期望的值

var4：表示需要修改为的新值

JUC包中的大部分功能都是依靠CAS完成的

synchronized、ReentrantLock都是悲观锁，认为操作代码的时候一定会发生冲突

tomcat内部使用线程池处理用户请求，如果很多请求都处于等待获取锁的状态，可能会耗尽tomcat线程池，
从而导致系统无法处理后面的请求，导致服务器处于不可用状态

目前在JDK的atomic包里提供了一个类AtomicStampedReference来解决CAS的ABA问题