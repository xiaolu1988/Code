package com.Paper.Parallelism;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tools {
    /* _@.time: 2022.4.23 周六 04:29 PM ..
     * Can seen,不通过打印的方式判断list(按照升序)有序。 '通过强逻辑代替肉眼去作判断了!'
     * */
    public static boolean isAscendingList(List<Integer> list) {
        boolean isAscended = true;
        int from = 0;
        int to = list.size() - 1;
        while (from < to) {
            if (Integer.compare(list.get(from), list.get(from+1)) <= 0) {
                from++;
            } else {
                isAscended = false;
                break;
            }
        }
        return isAscended;
    }

    /*
     * 随机生成多个正整数: */
    public static List<Integer> randomGeneratedNumbers(int range) {
        List<Integer> res = new ArrayList<>(range);
        int i = 0;
        for (;;) {
           if (i > range - 1)
               break;
           int number = new java.util.Random().nextInt(range);
           res.add(i, number + 1);
           i++;
        }
        return res;
    }

    /* To avoid '避免' 'partition()'代码出现duplicated的情况：
    * */
    public static int partition(List<Integer> sequence, int low, int high) {
        int low0 = low; int high0 = high;
        low0 = low0 + 1;
        int pivot = sequence.get(low);
        int left = low;

        while (low0 <= high0) {
            while (low0 <= high0 && sequence.get(low0) <= pivot)
                low0++;
            while (low0 <= high0 && sequence.get(high0) >= pivot)
                high0--;

            if (low0 > high0)
                break;
            /* 交换A[low]和A[high]位置上的元素: */
            // Exchange(series, low0, high0);      /*@.TODO: Collections.swap(serires,low0,high0)替换!*/
            Collections.swap(sequence, low0, high0);
        }
        Collections.swap(sequence, left, high0);
        return high0;
    }

    /* 插入排序:*/
    public static void insertionSort(List<Integer> sequence, int low, int high) {
        /* 5 1 9 3 7 4 8 6 2 */

        /* 1 5
        *  1 5 9
        *  1 5 9 3
        * */
        for (int i = low + 1; i <= high; i++) {
            int inserted = sequence.get(i);
            /*int j;
            for (j = i - 1; j >= 0 && inserted < sequence.get(j); j--) {    *//* 找到inserted的待插入位置（不是前一个，就是正好的位置!）:*//*
            }
            for (int k = i - 1; k > j; k--) {
                sequence.set(k+1, sequence.get(k));
            }
            sequence.set(j + 1, inserted);*/

            int toAdjust = i - 1;
            while (toAdjust >= 0 && (sequence.get(toAdjust) > inserted)) {  /* 通过与'partialAlreadySorted序列进行元素比较，然后交换。'*/
                Collections.swap(sequence, toAdjust, toAdjust+1);
                toAdjust--;
            }
        }
    }
}
