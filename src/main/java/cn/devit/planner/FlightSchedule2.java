package cn.devit.planner;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * 航班的单天计划。
 * <p>
 *
 *
 * @author lxb
 *
 */
public class FlightSchedule2 {

    Plane plane;

    String flightNumber;

    Leg leg;

    LocalTime departureTime;
    LocalDate departureDate;

    LocalTime arriavalTime;
    LocalDate arriavalDate;
    
    public DateTime getDepartureDateTime() {
        return new DateTime().withDate(departureDate).withTime(departureTime);
    }

    public Duration getFlyTime() {
        DateTime departure = new DateTime().withDate(departureDate)
                .withTime(departureTime);
        DateTime arriaval = new DateTime().withDate(arriavalDate)
                .withTime(arriavalTime);
        return new Duration(departure, arriaval);

    }

    @Override
    public String toString() {
        return flightNumber + " " + leg + " " + plane + " " + departureDate
                + " " + departureTime + "-" + arriavalDate + " " + arriavalTime;
    }
}
