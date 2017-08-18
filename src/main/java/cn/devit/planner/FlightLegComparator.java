package cn.devit.planner;

import static org.hamcrest.Matchers.instanceOf;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import cn.devit.planner.domain.AnchorPoint;
import cn.devit.planner.domain.Cancel;

public class FlightLegComparator implements Comparator<AnchorPoint> {

    @Override
    public int compare(AnchorPoint o1, AnchorPoint o2) {
        if (o1 instanceof Cancel) {
            if (o2 instanceof Cancel) {
                return 0;
            } else {
                return -1;
            }
        }
        if(o2 instanceof Cancel) {
            return 1;
        }
        if(o1 instanceof Plane) {
            if(o2 instanceof Plane) {
                return 0;
            }else {
                return -1;
            }
        }
        if(o2 instanceof Plane) {
            return 1;
        }
        FlightLeg left = (FlightLeg) o1;
        FlightLeg right = (FlightLeg) o2;

        return ComparisonChain.start().compare(left.weight, right.weight)
                //注意，这个反的，早离港的越急，晚离港的容易
                .compare(right.schedule.getDepartureDateTime(),
                        left.schedule.getDepartureDateTime())
                .compare(left.getFlyTime(), right.getFlyTime()).result();
    }

}
