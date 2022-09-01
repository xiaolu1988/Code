package com.Paper.Parallelism;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ThreadPoolQuicksortDriven {
    final static int N_CPU = Runtime.getRuntime().availableProcessors();
    final static int N = 1000000;    /* <-：问题规模: */
    static final int N_TEST = 1;    /* 测试次数：*/

    static final int THRESHOLD = 10000;

    /* 0. 保存一份最开始 '待排序序列'的副本： to adjustment of 'measureSortAlgorithmPerf()' 10 times iteration!
    * */ private static List<Integer> sequenceDuplicated = null;

    public static class FixedCounter {
        public static int counter = 0;
    }

    private static class AsyncPartitionByPivot implements Runnable {
        private final List<Integer> series;
        private final int low; private final int high;
        private ThreadPoolExecutor executor;

        public AsyncPartitionByPivot(ThreadPoolExecutor executor, List<Integer> numbers, int low, int high) {
            this(numbers, low, high);
            this.executor = executor;
        }
        private AsyncPartitionByPivot(List<Integer> series, int low, int high) {
            this.series = series;
            this.low = low; this.high = high;
        }
        @Override
        public void run() {
            if (low > high) {
                return;
            }
            /*if (high - low <= 10) {
                System.out.println("[]使用小数组优化!");
                *//* shutdown()何时调用也是一个问题：*//*
                if (!executor.isShutdown())
                    executor.shutdown();
                return;
            }*/
    /* _@.time: 2022.4.23 周6 02:59
             * Qs: 思考 'low;high' 索引设定是否存在逻辑问题: ?
             * */
            final int pos = Tools.partition(series, low, high);
            synchronized (executor) {
                FixedCounter.counter += 1;
            }
            if (FixedCounter.counter == series.size()) {
                if (!executor.isShutdown()) {
                    executor.shutdown();
                }
            }
            final Runnable left = new AsyncPartitionByPivot(executor, series, low, pos - 1);
            final Runnable right = new AsyncPartitionByPivot(executor, series, pos + 1, high);
            executor.execute(left); executor.execute(right);
        }
    }

    /* @00: 测试'ExecutorQsort'算法性能: */
    public static void measureExecutorQsortPerf() {
        //final int N = 100000;
        List<Integer> numbers = Tools.randomGeneratedNumbers(N);
        Collections.shuffle(numbers);

        List<Integer> origin = new ArrayList<>(numbers);    // 留一份拷贝:
        //Collections.copy(origin, numbers);

        /* 排序之前要确保Given的数据源是一致的: 否则，第一次sorted之后，第二次就是对一个alreadySorted的List作排序。往后都是这样!*/
        long fastest = Long.MAX_VALUE;
        long duration = 0;
        for (int i = 0; i < 1; i++) {
            if (i != 0) {
                numbers = new ArrayList<>(origin);  // 重新更新下数据源，to guarantee that待排序列是同一个!
            }
            final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, N_CPU*2, 10, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(N_CPU * 2),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            final AsyncPartitionByPivot r = new AsyncPartitionByPivot(executor, numbers, 0, numbers.size() - 1);

            long startTime = System.currentTimeMillis();
            executor.execute(r);
            while (!executor.isTerminated()) {
            }
            long endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            if (duration < fastest)
                fastest = duration;
            FixedCounter.counter = 0;   // 重新置为0，为了方便executor.shutdown()逻辑!
        }
        System.out.println("'ExecutorQsort:' Fastest execution done in " + fastest * (1.0) / 1000 + " s!");
    }

    /*  0. @.total: '测试框架':
     *  '比较各种算法的执行时间性能:' Qsort vs. ExecutorQsort and 'FjQsort':
     *      Therefore, 这样就把测试框架的代码统一到这一个地方来了。                   +_@.time: 2022.4.23 周六 22:45 night ..
     * */
    // measureSumPerf(Demo::sequentialSum, 100000); which is = 'measureSumPerf((Long range) -> Demo.sequentialSum(range), )'
    /*  'Review+重新理解了函数式接口实例的定义方法: ' [√]
     * */

    /*  Here存在一个问题：就是你执行了10次排序函数来统计最短的那个运行时间。但是第一次就排好序了。所以之后都是对 'AscendingOrder'的序列做快速排序。这个时候就会很缓慢了!
            但是，这无伤大雅的了。主体代码功能是实现了的!
     *
     *    @extra: Unless除非是你往深了挖掘，真的从那个递归深度的性能角度去分析它!       -_@.time: 2022.4.23 周六 23:01 night ..
    * */
    public static double measureAlgorithmPerf(Consumer<List<Integer>> c, List<Integer> sequence) {
        long fastest = Long.MAX_VALUE;
        long duration = 0;
        for (int i = 0; i < N_TEST; i++) {
            if (i != 0) {
                sequence = sequenceDuplicated;  /* After 1st iteration, 序列已经有序! 从第二次迭代使用最开始的副本:*/
            }
            long startTime = System.currentTimeMillis();
            c.accept(sequence);
            long endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            if (duration < fastest)
                fastest = duration;
        }
        return fastest * 1.0 / 1000;
    }


    /* 3. 基于ForkJoin的并行FjQuicksort实现:
    * */
    public static void FjQuicksort(List<Integer> numbers) {
        final ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new AsyncForkJoinQsortPartition(numbers));
    }

    /* 2. 基于Executor的并行Quicksort实现：
    * */
    public static void ExecutorQsort(List<Integer> numbers) {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, N_CPU * 2, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(N_CPU * 2),
                new ThreadPoolExecutor.CallerRunsPolicy());
        final AsyncPartitionByPivot task = new AsyncPartitionByPivot(executor, numbers, 0, numbers.size() - 1);

        executor.prestartAllCoreThreads();  /* 提前启动所有的Core工作线程：*/
        executor.execute(task);
        while (!executor.isTerminated()) {
        }
        FixedCounter.counter = 0;
    }

    /* 1. Quicksort的顺序实现:
     * */
    public static void Qsort(List<Integer> numbers) {
        sort0(numbers, 0, numbers.size() - 1);
    }
    private static void sort0(List<Integer> numbers, int low, int high) {
        if (low < high) {
            /* 优化小数组时候的方案：
            * */
            /*int length = high - low + 1;
            if (length <= THRESHOLD) {
                Tools.insertionSort(numbers, low, high);
                return;
            }*/

            int pos = Tools.partition(numbers, low, high);
            sort0(numbers, low, pos - 1);
            sort0(numbers, pos + 1, high);
        }
    }

    public static void main(String[] args) {
        //System.out.println("sizeOf(Cores): " + N_CPU);

        /* @.FunctionalTester:():功能性测试，确保逻辑正确!
        * */
        /*List<Integer> numbers = Arrays.asList(3,5,9,4,1,6,8,2,11);
        Qsort(numbers); System.out.println(numbers); //Tools.isAscendingList(numbers);

        Collections.shuffle(numbers);
        ExecutorQsort(numbers); System.out.println(numbers); //Tools.isAscendingList(numbers);*/

        List<Integer> rangeNumbers = Tools.randomGeneratedNumbers(N);
        ThreadPoolQuicksortDriven.sequenceDuplicated = new ArrayList<>(rangeNumbers);   /* 保留sequence序列副本!*/

        /*System.out.println("'2.FjQsort': Fastest done in " + measureAlgorithmPerf(ThreadPoolQuicksortDriven::FjQuicksort, rangeNumbers) + "s!");
        System.out.println("isAscended: " + Tools.isAscendingList(rangeNumbers));

        rangeNumbers = ThreadPoolQuicksortDriven.sequenceDuplicated;
        System.out.println("'1.ExecutorQsort': Fastest done in " + measureAlgorithmPerf(ThreadPoolQuicksortDriven::ExecutorQsort, rangeNumbers) + "s!");
        System.out.println("isAscended: " + Tools.isAscendingList(rangeNumbers));*/

        rangeNumbers = ThreadPoolQuicksortDriven.sequenceDuplicated;
        System.out.printf("\n'0.Qsort': Fastest done in %6.4f %s!\n" , measureAlgorithmPerf(ThreadPoolQuicksortDriven::Qsort, rangeNumbers), "s");
        System.out.println("isAscended: " + Tools.isAscendingList(rangeNumbers));
    }
}
