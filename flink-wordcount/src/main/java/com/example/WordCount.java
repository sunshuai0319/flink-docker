package com.example;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Flink WordCount示例程序
 * 使用硬编码的文本数据，统计单词出现的次数并输出到日志
 */
public class WordCount {

    // 使用线程安全的列表收集结果
    private static final CopyOnWriteArrayList<Tuple2<String, Integer>> RESULTS = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {
        // 设置执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        // 关闭输出到多个并行任务，使输出更加有序
        env.setParallelism(1);

        // 创建一个包含多个句子的列表（硬编码数据源）
        List<String> textList = Arrays.asList(
            "Apache Flink是一个框架和分布式处理引擎，用于在无边界和有边界数据流上进行有状态的计算",
            "Flink被设计为在所有常见的集群环境中运行，以内存速度和任何规模执行计算",
            "Flink提供了精确一次的状态一致性保证",
            "应用程序被并行化为可能数千个任务，这些任务分布在集群中并发执行",
            "Java和Scala是Flink的主要编程语言"
        );

        // 将列表转换为DataStream
        DataStream<String> textStream = env.fromCollection(textList);

        // 对每行文本进行分词，转换为(单词,1)的形式，然后按单词分组并统计
        DataStream<Tuple2<String, Integer>> wordCounts = textStream
            .flatMap(new Tokenizer())
            .keyBy(value -> value.f0)
            .sum(1);
        
        // 将结果添加到自定义收集器中
        wordCounts.addSink(new SinkFunction<Tuple2<String, Integer>>() {
            @Override
            public void invoke(Tuple2<String, Integer> value, Context context) {
                RESULTS.add(value);
            }
        });
        
        // 执行程序
        env.execute("Flink Word Count Example");
        
        // 等待1秒确保所有结果都已收集
        Thread.sleep(1000);
        
        // 对结果进行排序（按出现次数降序）
        List<Tuple2<String, Integer>> sortedResults = new ArrayList<>(RESULTS);
        Collections.sort(sortedResults, new Comparator<Tuple2<String, Integer>>() {
            @Override
            public int compare(Tuple2<String, Integer> o1, Tuple2<String, Integer> o2) {
                return o2.f1 - o1.f1;  // 降序排列
            }
        });

        // 在程序执行完成后，以格式化方式打印结果
        System.out.println("\n\n");
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                                                        【词频统计结果】                                                                           ┃");
        System.out.println("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
        System.out.println("┃                         单词                            ┃                                      出现次数                                          ┃");
        System.out.println("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
        
        for (Tuple2<String, Integer> result : sortedResults) {
            System.out.printf("┃ %-50s ┃ %-80d ┃\n", result.f0, result.f1);
        }
        
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println("\n【原始文本】");
        
        for (int i = 0; i < textList.size(); i++) {
            System.out.println("文本 #" + (i+1) + ": " + textList.get(i));
        }
        System.out.println("\n");
    }

    /**
     * 自定义FlatMapFunction，用于将文本行分解为单词
     */
    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // 将每行文本按空格分词，并过滤掉长度小于2的单词
            String[] words = value.toLowerCase().split("\\s+");
            
            for (String word : words) {
                if (word.length() > 1) {
                    out.collect(new Tuple2<>(word, 1));
                }
            }
        }
    }
} 