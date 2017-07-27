package cn.devit.demo.planner;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class SeatAssignment {

    Seat seat;

    Passenger passenger;

    @PlanningVariable(valueRangeProviderRefs = { "seatRange" }, nullable = true)
    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public SeatAssignment() {
        super();
    }

    @Override
    public String toString() {
        return passenger + " now @" + seat;
    }

    public SeatAssignment(Passenger passenger) {
        super();
        this.passenger = passenger;
    }

    public SeatAssignment(Passenger passenger,Seat seat) {
        super();
        this.passenger = passenger;
        this.seat = seat;
        passenger.actSeat = seat;
    }

}
