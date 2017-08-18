package cn.devit.planner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import cn.devit.planner.domain.Cancel;

public class FlightLegComparatorTest {
    
    @Test
    public void cancel_first() throws Exception {
        Cancel c = new Cancel();
        Plane p = new Plane("1");
        FlightLeg flight = new FlightLeg();
        int result = new FlightLegComparator().compare(c, p);
        assertThat(result, is(-1));
        result = new FlightLegComparator().compare(c, flight);
        assertThat(result, is(-1));
        
        result = new FlightLegComparator().compare(p, c);
        assertThat(result, is(1));

    }
    
    @Test
    public void plane_eq() throws Exception {
        Plane p1 = new Plane("1");
        Plane p2 = new Plane("1");
        int result = new FlightLegComparator().compare(p1, p2);
        assertThat(result, is(0));
    }

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
