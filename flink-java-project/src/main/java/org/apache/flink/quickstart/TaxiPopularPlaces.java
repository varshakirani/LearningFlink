package org.apache.flink.quickstart;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.quickstart.TaxiRideFiltering;
import scala.Tuple2;
import scala.Tuple5;
import javax.xml.crypto.Data;

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

        DataStream<Tuple2<Integer,Boolean>> popularPlaces = rides
                .filter(new TaxiRideFiltering.checkInNYC())
                .map(new GridCell());
            popularPlaces.print();
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
}
