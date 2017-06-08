package org.apache.flink.quickstart;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.quickstart.TaxiRideFiltering;
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
        DataStream<TaxiRide> filteredRides = rides.filter(new TaxiRideFiltering.checkInNYC());

        env.execute();
    }
}
