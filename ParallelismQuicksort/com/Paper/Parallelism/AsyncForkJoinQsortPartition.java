package com.Paper.Parallelism;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public class AsyncForkJoinQsortPartition extends RecursiveAction {
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
        /*if (low > high)
            return;*/
        if (low <= high) {
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
