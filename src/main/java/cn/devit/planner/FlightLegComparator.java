package cn.devit.planner;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;


public class FlightLegComparator implements Comparator<FlightLeg> {

    @Override
    public int compare(FlightLeg o1, FlightLeg o2) {
        if (o1 instanceof NullFlightLeg) {
            if (o2 instanceof NullFlightLeg) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (o2 instanceof NullFlightLeg) {
                return 1;
            } else {
                return ComparisonChain.start()
                .compare(o1.weight, o2.weight)
                //注意，这个反的，早离港的越急，晚离港的容易
                .compare(o2.schedule.getDepartureDateTime(),o1.schedule.getDepartureDateTime())
                .compare(o1.getFlyTime(), o2.getFlyTime())
                .result()
                ;
            }
        }
    }

}
