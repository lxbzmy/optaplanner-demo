package cn.devit.planner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

public class FlightLegComparatorTest {

    @Test
    public void compare() throws Exception {
        FlightLeg flightLeg = new FlightLeg();
        FlightSchedule2 s1 = new FlightSchedule2();
        s1.departureDate = new LocalDate("2017-10-01");
        s1.departureTime = new LocalTime(10, 10);
        s1.arriavalDate = new LocalDate("2017-10-01");
        s1.arriavalTime = new LocalTime(11, 10);
        s1.flightNumber = "CA999";
        s1.leg = new Leg("PEK", "SHA");
        s1.plane = new Plane("1");
        flightLeg.reset(s1);

        FlightLeg flightLeg2 = new FlightLeg();
        FlightSchedule2 s2 = new FlightSchedule2();
        s2.departureDate = new LocalDate("2017-10-02");
        s2.departureTime = new LocalTime(10, 10);
        s2.arriavalDate = new LocalDate("2017-10-01");
        s2.arriavalTime = new LocalTime(11, 10);
        s2.flightNumber = "CA999";
        s2.leg = new Leg("PEK", "SHA");
        s2.plane = new Plane("1");
        flightLeg2.reset(s2);

        int result = new FlightLegComparator().compare(flightLeg, flightLeg2);
        assertThat(result, is(1));
    }
    
    @Test
    public void compare_weight() throws Exception {
        FlightLeg flightLeg = new FlightLeg();
        flightLeg.weight = 1;
        FlightSchedule2 s1 = new FlightSchedule2();
        s1.departureDate = new LocalDate("2017-10-01");
        s1.departureTime = new LocalTime(10, 10);
        s1.arriavalDate = new LocalDate("2017-10-01");
        s1.arriavalTime = new LocalTime(11, 10);
        s1.flightNumber = "CA999";
        s1.leg = new Leg("PEK", "SHA");
        s1.plane = new Plane("1");
        flightLeg.reset(s1);

        FlightLeg flightLeg2 = new FlightLeg();
        flightLeg2.weight = 2;
        FlightSchedule2 s2 = new FlightSchedule2();
        s2.departureDate = new LocalDate("2017-10-02");
        s2.departureTime = new LocalTime(10, 10);
        s2.arriavalDate = new LocalDate("2017-10-01");
        s2.arriavalTime = new LocalTime(11, 10);
        s2.flightNumber = "CA999";
        s2.leg = new Leg("PEK", "SHA");
        s2.plane = new Plane("1");
        flightLeg2.reset(s2);

        int result = new FlightLegComparator().compare(flightLeg, flightLeg2);
        assertThat(result, is(-1));
    }
}
