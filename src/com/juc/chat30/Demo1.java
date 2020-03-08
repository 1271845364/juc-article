package com.juc.chat30;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

/**
 * @author jinhui.ye@ucarinc.com
 * @date 2019/10/15
 */
public class Demo1 {

    /**
     * 没有返回值
     */
    public static void runAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("runAsync");
        });
        System.out.println(future.get());
    }

    /**
     * 有返回值
     */
    public static void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> supplyAsync = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("supplyAsync");
            return System.currentTimeMillis();
        });

        System.out.println("time=" + supplyAsync.get());
    }

    /**
     * 当前线程继续执行whenComplete的任务
     *
     * @throws InterruptedException
     */
    public static void whenComplete() throws InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (new Random().nextInt() % 2 >= 0) {
                int i = 12 / 0;
            }
            System.out.println("run end ...");
        });

        future.whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void aVoid, Throwable throwable) {
                System.out.println("执行完成");
            }
        });

        future.exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                System.out.println("执行失败" + throwable.getMessage());
                return null;
            }
        });

        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * 当一个线程依赖另一个线程时，可以使用thenApply方法来把这两个线程串行化
     */
    private static void thenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                long result = new Random().nextInt(100);
                System.out.println("result=" + result);
                return result;
            }
        }).thenApply(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) {
                String result = "s" + aLong;
                System.out.println("result=" + result);
                return result;
            }
        });
        System.out.println(future.get());
    }

    /**
     * handle 是执行任务完成时对结果的处理。
     * handle 方法和 thenApply 方法处理方式基本一样。不同的是 handle 是在任务完成后再执行，
     * 还可以处理异常的任务。thenApply 只可以执行正常的任务，任务出现异常则不执行 thenApply 方法。
     */
    private static void handle() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = 10 / 0;
                return new Random().nextInt(10);
            }
        }).handle(new BiFunction<Integer, Throwable, Integer>() {
            @Override
            public Integer apply(Integer integer, Throwable throwable) {
                int result = -1;
                if (throwable == null) {
                    result = integer * 2;
                } else {
                    System.out.println(throwable.getMessage());
                }
                return result;
            }
        });
        System.out.println(handle.get());
    }

    /**
     * 接收任务的处理结果，并消费处理，无返回结果
     */
    private static void thenAccept() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return new Random().nextInt(10);
            }
        }).thenAccept(i -> {
            System.out.println(i);
        });
        System.out.println(future.get());
    }

    /**
     * 跟 thenAccept 方法不一样的是，不关心任务的处理结果。只要上面的任务执行完成，就开始执行 thenRun 。
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return new Random().nextInt(10);
            }
        }).thenRun(() -> {
            System.out.println("theRun...");
        });

        System.out.println(future.get());
    }

    /**
     * thenCombine会把两个CompletionStage的任务都执行完成后，把两个任务的结果一块交给thenCombine来处理
     */
    private static void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "hello";
            }
        }).thenCombine(CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "world";
            }
        }), new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) {
                return s + s2;
            }
        });
        System.out.println(future.get());
    }

    /**
     * 当两个CompletableStage都执行完成后，把结果一块交给thenAcceptBoth来进行消耗
     */
    private static void thenAcceptBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        }).thenAcceptBoth(CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        }), new BiConsumer<Integer, Integer>() {
            @Override
            public void accept(Integer integer, Integer integer2) {
                System.out.println(integer + integer2);
            }
        });
        System.out.println(future.get());
    }

    /**
     * 两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的转化操作
     */
    private static void applyToEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(30);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        }).applyToEither(CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(30);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        }), new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return 2 * integer;
            }
        });

        System.out.println(future.get());
    }

    /**
     * 两个CompletionStage，谁执行返回的结果快，我就用哪个CompletionStage的结果进行下一步的消耗操作
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void acceptEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        }).acceptEither(CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        }), new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer);
            }
        });

        System.out.println(future.get());
    }

    /**
     * 两个CompletionStage，任何一个完成都会执行下一步操作(Runnable)
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void runAfterEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        }).runAfterEither(CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        }), () -> {
            System.out.println("上面有一个已经完成");
        });

        System.out.println(future.get());
    }

    /**
     * 两个CompletionStage都完成了计算才会执行下一步操作(Runnable)
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void runAfterBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        }).runAfterBoth(CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        }), () -> {
            System.out.println("上面两个已经完成");
        });

        System.out.println(future.get());
    }

    /**
     * thenCompose方法允许你对两个CompletionStage进行流水线操作，第一个操作完成之后，将其结果作为参数传递给第二个操作
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        }).thenCompose(new Function<Integer, CompletionStage<Integer>>() {
            @Override
            public CompletionStage<Integer> apply(Integer integer) {
                return CompletableFuture.supplyAsync(new Supplier<Integer>() {
                    @Override
                    public Integer get() {
                        int t = integer * 2;
                        System.out.println("f2=" + t);
                        return t;
                    }
                });
            }
        });

        System.out.println("thenCompose result=" + future.get());
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        runAsync();
//        supplyAsync();
//        whenComplete();
//        thenApply();
//        handle();//在handle中可以根据任务是否有异常来进行做相应的后续处理操作，而thenApply方法，如果上个任务出现错误，则不会执行thenApply方法
//        thenAccept();//只是消费执行完成的任务，并可以根据上面的任务返回的结果进行处理。并没有后续的出错操作
        /**
         * 输出结果：
         * 8
         * null
         *
         */

//        thenRun();//和thenAccept方法类似。不同的是上个任务处理完成后，并不会把计算的结果传给thenRun方法。只是处理完任务后，执行thenRun的后续操作
        /**
         * 输出结果：
         * theRun...
         * null
         *
         */

//        thenCombine();//thenCombine会把两个CompletionStage的任务都执行完成后，把两个任务的结果一块交给thenCombine来处理
        /**
         * 输出结果：
         * helloworld
         */

//        thenAcceptBoth();//当两个CompletionStage都执行完成后，把结果一块交给thenAcceptBoth来进行消耗
        /**
         * 输出结果：
         * f1=0
         * f2=2
         * 2
         * null
         */

//        applyToEither();//两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的转化操作
        /**
         * 输出结果：
         * f1=14
         * f2=5
         * 28
         *
         */

//        acceptEither();//两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的消耗操作。
        /**
         * 输出结果：
         * f1=1
         * 1
         * null
         *
         */

//        runAfterEither();//两个CompletionStage，任何一个完成都会执行下一步操作(Runnable)
//        TimeUnit.SECONDS.sleep(5);
        /**
         * 输出结果：
         * f1=1
         * 上面有一个已经完成
         * null
         * f2=2
         *
         */

//        runAfterBoth();//两个CompletionStage，都完成了计算才会执行下一步操作(Runnable)
        /**
         * 输出结果：
         * f1=2
         * f2=2
         * 上面两个已经完成
         * null
         */

//        thenCompose();//thenCompose方法允许你对两个CompletionStage进行流水线操作，第一个操作完成之后，将其结果作为参数传递给第二个操作
        /**
         * 输出结果：
         * f1=1
         * f2=2
         * thenCompose result=2
         */
    }

}