package cn.devit.planner;

import org.joda.time.DateTime;

public class UnScheduleFlight {

    boolean crossBroad;
    String flightNumber;
    Plane plane;
    Leg leg;
    DateTime departureDateTime;
    DateTime arrivalDateTime;

    boolean emptyFlight;

    @Override
    public String toString() {
        return "XX" + plane + " " + leg + " " + departureDateTime + " "
                + arrivalDateTime;
    }
}
