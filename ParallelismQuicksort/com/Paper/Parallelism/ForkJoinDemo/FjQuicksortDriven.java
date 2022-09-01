package com.Paper.Parallelism.ForkJoinDemo;

import com.Paper.Parallelism.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

public class FjQuicksortDriven {
    final static int N = 100000;
    private static final ForkJoinPool pool = new ForkJoinPool();
    public static void main(String[] args) {
        List<Integer> numbers = Tools.randomGeneratedNumbers(N);
        List<Integer> numbersCopy = new ArrayList<>(numbers);
        //log(numbers.toString());

        measureAlgorithmPerf(FjQuicksortDriven::sort, numbers);
        log(String.valueOf(Tools.isAscendingList(numbers)));
        //System.out.println(numbers);
    }

    private static void log(String msg) {
        System.out.println(msg);
    }

    /* 基于ForkJoin框架的FjQuicksort快排算法： _@.time: 2022.4.24 周日 23：45 night ..
    * */
    public static void sort(List<Integer> numbers) {
        pool.invoke(new AsyncForkJoinQsortPartition(numbers));
    }

    /* 测试框架:*/
    public static void measureAlgorithmPerf(Consumer<List<Integer>> c, List<Integer> sequence) {
        long fastest = Long.MAX_VALUE;
        long duration;
        int i = 0;
        for (;;) {
            if (i > 0)  /* 具体迭代次数：*/
                break;
            long startTime = System.currentTimeMillis();
            c.accept(sequence);
            long endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            if (duration <= fastest)
                fastest = duration;
            i++;
        }
        System.out.println("2.'FjQuicksort' Fastest Done in " + fastest * 1.0 / 1000 + "s!");
    }

    private static class AsyncForkJoinQsortPartition extends RecursiveAction {
        private final List<Integer> sequence;
        private final int low;
        private final int high;
        private static final int THRESHOLD = 10;   /* 最小任务阈值:*/

        public AsyncForkJoinQsortPartition(List<Integer> sequence) {
            this(sequence, 0, sequence.size() - 1);
        }
        private AsyncForkJoinQsortPartition(List<Integer> sequence, int low, int high) {
            this.sequence = sequence;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            /*int length = high - low + 1;    *//* which differs 'ForkJoinSumCalculator'*//*
            if (length <= 1)
                return;*/
            if (low > high)
                return;

            int splitPosition = Tools.partition(sequence, low, high);
            /* 作''递归任务''划分：
            * */
            AsyncForkJoinQsortPartition leftTask = new AsyncForkJoinQsortPartition(sequence, low, splitPosition - 1);
            AsyncForkJoinQsortPartition rightTask = new AsyncForkJoinQsortPartition(sequence, splitPosition + 1, high);
            leftTask.fork();
            rightTask.compute();
            leftTask.join();
        }
    }
}