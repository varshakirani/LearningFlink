package org.apache.flink.quickstart;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * Created by Varsha Kirani on 6/6/2017.
 */
public class Demo1 {
public static void main(String[] args)throws Exception{
   final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream<String> words = env.fromElements("12","11","10","09","08");

    DataStream<Integer> parsed = words.map(new MapFunction<String, Integer>() {
        @Override
        public Integer map(String s) throws Exception {
            return Integer.parseInt(s);
        }
    });

    parsed.writeAsText("C:\\Users\\Varsha Kirani\\Documents\\Work\\DFKI\\LearningFlink\\flink-java-project\\target\\results\\demo1.txt"    );
    env.execute();
}
}
