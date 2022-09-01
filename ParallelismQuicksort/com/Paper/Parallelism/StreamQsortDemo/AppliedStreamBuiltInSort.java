package com.Paper.Parallelism.StreamQsortDemo;

import com.Paper.Parallelism.Tools;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppliedStreamBuiltInSort {

    /* @.time;2022.4.24 周日 10:01 night ..
     * 测试Stream<T>接口内置的sorted函数:
    * */
    public static void main(String[] args) {
        List<Integer> sequence =
        IntStream.rangeClosed(1, 20).mapToObj(Integer::new)
                                     .sorted(new Comparator<Integer>() {
                                         /* 0. 逆序比较器:*/
                                         @Override
                                         public int compare(Integer a, Integer b) {
                                             if (a == b)
                                                 return 0;
                                             else if (a < b)
                                                 return 1;
                                             else
                                                 return -1;
                                         }
                                     })
                                     .collect(Collectors.toList());
        System.out.println(sequence);

        List<Integer> numbers = Tools.randomGeneratedNumbers(20);
        System.out.println("\n随机序列: "+numbers);
        List<Integer> sortedNumbers = numbers.stream().sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 == o2)
                    return 0;
                else if (o1 < o2)
                    return 1;
                else
                    return -1;
            }
        }).collect(Collectors.toList());
        System.out.println(sortedNumbers);

        /* 1. stream of mind thoughts;
         *  在厘清楚java.util.stream.Stream<T>接口的内置sorted()中间操作之后，下面就考虑Spliterator<T>这个迭代器接口是否可以做到expectation呢? @.time: 22:49
         * @.[猜测:] 应该是直接用哪个StreamSupport.stream(Spliterator<T>)就可以了。i.e. 用这个迭代器初始化这个流即可。    // 0. 基于Java8并行流的Quicksort: 1. 基于ForkJoin框架的FjQuicksort算法。[√]
        * */

    }
}
