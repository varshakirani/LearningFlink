package org.apache.flink.quickstart;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Created by Varsha Kirani on 6/6/2017.
 */
public class TaxiRideFiltering {
    public static void main(String[] args) throws Exception{
        //get an ExecutionEnvironment
        StreamExecutionEnvironment env =  StreamExecutionEnvironment.getExecutionEnvironment();
        //configure event-time processing
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        //get the taxi ride data stream from nycTaxiRides.gz
        //GZIPInputStream is a flink function that reads gz file. It is been called in TaxiRideSource class
        final int maxDelay = 60; //events are out of order by max 60seconds
        final int servingSpeed = 6000; //events of 100 minutes are served in 1 second
        DataStream<TaxiRide> rides = env.addSource(new TaxiRideSource("C:\\Users\\Varsha Kirani\\Documents\\Work\\DFKI\\LearningFlink\\nycTaxiRides.gz",maxDelay,servingSpeed));

        DataStream<TaxiRide> filteredRides = rides.filter(new checkInNYC());
        filteredRides.writeAsText("C:\\Users\\Varsha Kirani\\Documents\\Work\\DFKI\\LearningFlink\\flink-java-project\\target\\results\\TaxiRideFiltering.txt");
        env.execute();

    }

    public static class checkInNYC implements FilterFunction<TaxiRide> {

        @Override
        public boolean filter(TaxiRide rides) throws Exception {
            return GeoUtils.isInNYC(rides.startLon,rides.startLat) && GeoUtils.isInNYC(rides.endLon,rides.endLat);
        }
    }
}
