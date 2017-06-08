package org.apache.flink.quickstart;

import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import com.dataartisans.flinktraining.exercises.datastream_java.basics.RideCleansing;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.scala.KeyedStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * Created by Varsha Kirani on 6/8/2017.
 */
public class TaxiPopularPlaces {
    public static void main(String[] args)throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        final int maxDelay = 60; //events are out of order by max 60seconds
        final int servingSpeed = 6000; //events of 100 minutes are served in 1 second
        DataStream<TaxiRide> rides = env.addSource(new TaxiRideSource("C:\\Users\\Varsha Kirani\\Documents\\Work\\DFKI\\LearningFlink\\nycTaxiRides.gz",maxDelay,servingSpeed));

        DataStream<Tuple4<Integer,Long,Boolean,Integer>> gridPlaces = rides
                .filter(new TaxiRideFiltering.checkInNYC())
                .map(new GridCell())
                .<KeyedStream < Tuple2 <Integer, Boolean>, Tuple2 <Integer,Boolean> > >keyBy(0,1)
                .timeWindow(Time.minutes(15),Time.minutes(5))
                .apply(new placeCounter());



            gridPlaces.print();
            env.execute();
    }

    public static class GridCell implements MapFunction <TaxiRide, Tuple2<Integer,Boolean>>{

        @Override
        public Tuple2<Integer, Boolean> map(TaxiRide taxiRide) throws Exception {
           if(taxiRide.isStart){
            return new Tuple2<>(GeoUtils.mapToGridCell(taxiRide.startLon,taxiRide.startLat),true) ;
           }
           else{
               return new Tuple2<>(GeoUtils.mapToGridCell(taxiRide.startLon,taxiRide.startLat),false) ;
           }
        }
    }

    public static class placeCounter implements WindowFunction<
            Tuple2<Integer,Boolean>,
            Tuple4<Integer,Long,Boolean,Integer>,
            Tuple ,
            TimeWindow >{

        @SuppressWarnings("unchecked")
        @Override
            public void apply(Tuple tuple, TimeWindow window, Iterable<Tuple2<Integer, Boolean>> input, Collector<Tuple4<Integer, Long, Boolean, Integer>> out) {
            int cellId = ((Tuple2<Integer,Boolean>)tuple).f0;
            Long windowTime = window.getEnd();
            Boolean isStart = ((Tuple2<Integer,Boolean>)tuple).f1;
            int counts = 0;
            for( Tuple2<Integer,Boolean> v: input){ counts += 1;}

            out.collect(new Tuple4<>(cellId,windowTime,isStart,counts));
        }
    }
}
