package cn.devit.demo.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class SeatPlan implements Solution<Score<HardSoftScore>> {

    List<Seat> seats;

    List<Passenger> passengers;

    List<SeatAssignment> designations;

    List<Friends> friends;

    @ValueRangeProvider(id = "seatRange")
    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    @PlanningEntityCollectionProperty
    public List<SeatAssignment> getDesignations() {
        return designations;
    }

    public void setDesignations(List<SeatAssignment> designations) {
        this.designations = designations;
    }

    Score<HardSoftScore> score;

    public Score<HardSoftScore> getScore() {
        return score;
    }

    public void setScore(Score<HardSoftScore> score) {
        this.score = score;
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(EnumSet.allOf(SeatFeature.class));
        facts.addAll(seats);
        facts.addAll(getFriends());
        return facts;
    }

    public List<Friends> getFriends() {
        return friends;
    }

    public void setFriends(List<Friends> friends) {
        this.friends = friends;
    }

}
