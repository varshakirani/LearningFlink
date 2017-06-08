package org.apache.flink.quickstart;


import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import java.util.*;

/**
 * Created by Varsha Kirani on 6/8/2017.
 * Example for Basic Data Sorces: Collections
 *
 */
public class Demo2 {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> names = env.fromElements("Varsha","Kirani");

        List<String> namesList = new ArrayList<String>();
        namesList.add("Varsha");
        namesList.add("Kirani");
        namesList.add("Gopinath");

        DataStream<String> names2 = env.fromCollection(namesList);

        names.print();
        names2.print();

        env.execute();
    }
}
