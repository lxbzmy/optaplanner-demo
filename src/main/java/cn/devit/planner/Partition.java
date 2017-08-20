package cn.devit.planner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import cn.devit.planner.Edge.EdgeType;
import cn.devit.planner.domain.AnchorPoint;
import cn.devit.planner.domain.Cancel;

public class Partition implements SolutionPartitioner<FlightSolution> {

    @Override
    public List<FlightSolution> splitWorkingSolution(
            ScoreDirector<FlightSolution> scoreDirector,
            Integer runnablePartThreadLimit) {

        FlightSolution rootSolution = scoreDirector.getWorkingSolution();

        FlightSolution partSolution = new FlightSolution();
        partSolution.airportCloseTime = rootSolution.airportCloseTime;
        partSolution.weathers = rootSolution.weathers;
        partSolution.legs = rootSolution.legs;
        partSolution.planeLegConstraints = rootSolution.planeLegConstraints;

        
        List<FlightSolution> parts = new ArrayList<FlightSolution>();
        for (FlightLeg item : rootSolution.left) {
            Log.d("方案分区{}", item.plane);
            
            Plane plane = clone(item.plane);
            
            FlightLeg left = clone(item);
            
            left.setNextFlight(item.getNextFlight());

//            Predicate<FlightLeg> filter = new Predicate<FlightLeg>() {
//                @Override
//                public boolean apply(FlightLeg input) {
//                    return input.plane.equals(item.plane);
//                }
//            };
            partSolution.left = new Edge(EdgeType.left);
            partSolution.left.add(left);
            
            //join
            plane.setNextFlight(left);
            left.setPreviousLeg(plane);
            left.setPlane(plane);
            
            List<AnchorPoint> anchors = new ArrayList<>();
            anchors.add(plane);
            anchors.add(new Cancel("XX"));
            partSolution.anchors = anchors;
            
            
            List<FlightLeg> subFlights = new ArrayList<FlightLeg>();
            subFlights.add(left);
            FlightLeg right = null;
            while(left.getNextFlight()!=null) {
                right = left.getNextFlight();
                right = clone(right);
                left.setNextFlight(right);
                right.setPreviousLeg(left);
                right.setPlane(plane);
                
                left = right;
                subFlights.add(left);
            }
            
            
            partSolution.right = new Edge(EdgeType.right);
            partSolution.right
                    .add(right);

            partSolution.flights = subFlights;
            partSolution.planes = new ArrayList<>();
            partSolution.planes.add(plane);
            parts.add(partSolution);
        }
        return parts;
    }

    private FlightLeg clone(FlightLeg item) {
        FlightLeg left = new FlightLeg(item.id);
        left.crossBorder = item.crossBorder;
        left.schedule = item.schedule;
        left.departureAirportArrivalTime = item.departureAirportArrivalTime;
        left.flyTime = item.flyTime;
        left.plane = item.plane;
        left.previousLeg = item.previousLeg;
        left.schedule = item.schedule;
        left.stayMinutes = item.stayMinutes;
        left.weight = item.weight;
        return left;
    }

    private Plane clone(Plane plane) {
        Plane bean = new Plane(plane.id);
        bean.setModel(plane.getModel());
        return bean;
    }

}
