package cn.devit.planner.domain;

import org.joda.time.LocalTime;

import cn.devit.planner.Leg;

public class FlightSchedule {

    String flightNumber;
    Leg leg;
    LocalTime departureTime;
    LocalTime arriavalTime;
    Frequency frequency;
}
