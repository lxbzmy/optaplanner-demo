package cn.devit.planner;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import cn.devit.planner.Edge.EdgeType;

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

            Predicate<FlightLeg> filter = new Predicate<FlightLeg>() {
                @Override
                public boolean apply(FlightLeg input) {
                    return input.plane.equals(item.plane);
                }
            };
            partSolution.left = new Edge(EdgeType.left);
            partSolution.left.add(item);
            partSolution.right = new Edge(EdgeType.right);
            partSolution.right
                    .addAll(Collections2.filter(rootSolution.right, filter));
            partSolution.startLegs = new ArrayList<>();
            partSolution.startLegs.add(item);
            partSolution.startLegs.add(new NullFlightLeg());

            partSolution.flights = new ArrayList<>(
                    Collections2.filter(rootSolution.flights, filter));
            partSolution.planes = new ArrayList<>();
            partSolution.planes.add(item.plane);
            parts.add(partSolution);
        }
        return parts;
    }

}
