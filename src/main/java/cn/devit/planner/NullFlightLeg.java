package cn.devit.planner;

public final class NullFlightLeg extends FlightLeg {

    public NullFlightLeg() {
        setId("0");
        FlightSchedule2 sc = new FlightSchedule2();
        this.schedule = sc;
    }

    @Override
    public String toString() {
        return id + " 空航段";
    }
}
