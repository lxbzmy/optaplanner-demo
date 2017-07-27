package cn.devit.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import cn.devit.planner.constraints.Effect;
import cn.devit.planner.constraints.Weather;
import cn.devit.planner.domain.FlightSchedule;
import cn.devit.planner.domain.FlightTime;

public class Main {

    static List<Leg> legs = new ArrayList<Leg>();;
    private static List<Plane> planes = new ArrayList<Plane>();;

    public static void main(String[] args) {

        // Build the Solver
        SolverFactory<FlightSolution> solverFactory = SolverFactory
                .createFromXmlInputStream(
                        Main.class.getResourceAsStream("flight.xml"));
        Solver<FlightSolution> solver = solverFactory.buildSolver();

        FlightSolution plan = new FlightSolution();

        Leg leg_1_2 = leg("1", "2");
        Leg leg_2_1 = leg("2", "1");
        Leg leg_1_3 = leg("1", "3");
        Leg leg_1_4 = leg("1", "4");
        Leg leg_2_3 = leg("2", "3");
        Leg leg_3_2 = leg("3", "2");
        Leg leg_2_4 = leg("2", "4");
        Leg leg_3_4 = leg("3", "4");
        plan.legs = legs;

        Plane plane1 = plane("1");
        //        Plane plane2 = plane("2");
        //        Plane plane3 = plane("3");
        //        Plane plane4 = plane("4");
        //        Plane plane5 = plane("5");
        //        Plane plane6 = plane("6");
        plan.planes = planes;

        List<FlightLeg> flight = new ArrayList<FlightLeg>();
        List<FlightLeg> startPoint = new ArrayList<FlightLeg>();

        FlightLeg flight_empty = new NullFlightLeg();
        //first data
        FlightLeg f1_leg1_2 = flight("1", plane1, "CA101", leg_1_2,
                "2017-10-10T09:05:00", "2017-10-10T11:50:00");

        startPoint.add(f1_leg1_2);
        startPoint.add(flight_empty);
        //        flight.add(f1_leg1_2);
        FlightLeg f1_leg2_3 = flight("2", plane1, "CA101", leg_2_3,
                "2017-10-10T13:00:00", "2017-10-10T15:00:00");
        flight.add(f1_leg2_3);
        //        f1_leg1_2.nextLeg = f1_leg2_3;

        FlightLeg f1_leg3_2 = flight("3", plane1, "CA102", leg_3_2,
                "2017-10-10T16:50:00", "2017-10-10T18:40:00");
        flight.add(f1_leg3_2);
        //        f1_leg2_3.nextLeg = f1_leg3_2;
        //        f1_leg3_2.previousLeg = f1_leg2_3;
        //        f1_leg2_3.nextLeg = f1_leg3_2;
        f1_leg3_2.previousLeg = f1_leg1_2;
        f1_leg1_2.nextLeg = f1_leg3_2;
        FlightLeg f1_leg2_1 = flight("4", plane1, "CA102", leg_2_1,
                "2017-10-10T19:40:00", "2017-10-10T22:25:00");
        //        f1_leg3_2.nextLeg = f1_leg2_1;
        //        f1_leg2_1.previousLeg = f1_leg3_2;
        flight.add(f1_leg2_1);

        plan.flights = flight;
        plan.startLegs = startPoint;

        weather(new Airport("2"), Effect.departure, "2017-10-10T12:00:00",
                "2017-10-10T13:10:00");
        plan.weathers = weathers;

        List<PlaneLegConstraint> cons = new ArrayList<PlaneLegConstraint>();
        plan.planeLegConstraints = cons;

        plan.dateRange = dates("2017-10-10", "2017-10-11");
        plan.clocks = clocks();

        flightTime(plane1, leg_1_2, 55 + 60 + 50);
        flightTime(plane1, leg_2_3, 2 * 60);
        flightTime(plane1, leg_1_2, 60 + 50);
        flightTime(plane1, leg_1_2, 20 + 60 + 60 + 25);

        solver.solve(plan);
        plan = solver.getBestSolution();

        // Display the result
        System.out.println("\nResult:\n");
        System.out.println(toString(plan));

    }

    private static FlightLeg flight(String id, Plane plane1,
            String flightNumber, Leg leg, String departure, String arrival) {
        FlightSchedule2 sc = new FlightSchedule2();
        sc.flightNumber = flightNumber;
        DateTime dateTime = new DateTime(departure);
        sc.departureDate = new LocalDate(dateTime);
        sc.departureTime = new LocalTime(dateTime);

        dateTime = new DateTime(arrival);
        sc.arriavalDate = new LocalDate(dateTime);
        sc.arriavalTime = new LocalTime(dateTime);
        sc.leg = leg;
        FlightLeg flight = new FlightLeg(id);
        flight.departureDate = sc.departureDate;
        flight.departureTime = sc.departureTime;
        flight.flyTime = sc.getFlyTime();
        sc.plane = plane1;

        flight.plane = sc.plane;
        flight.schedule = sc;
        return flight;

    }

    static Table<Leg, Plane, Duration> flightTimeTable = Tables.newCustomTable(
            Maps.<Leg, Map<Plane, Duration>> newLinkedHashMap(),
            new Supplier<Map<Plane, Duration>>() {
                public Map<Plane, Duration> get() {
                    return Maps.newLinkedHashMap();
                }
            });

    static Set<FlightTime> flightTimes = new HashSet<FlightTime>();

    static FlightTime flightTime(Plane plane, Leg leg, int minus) {
        FlightTime t = new FlightTime(plane, leg,
                new Duration(1000L * 60 * minus));
        flightTimes.add(t);
        flightTimeTable.row(leg).put(plane, t.getDuration());
        return t;
    }

    static Plane plane(String string) {
        Plane plane = new Plane(string);
        planes.add(plane);
        return plane;
    }

    static Leg leg(String from, String to) {
        Leg leg = new Leg(from, to);
        legs.add(leg);
        return leg;
    }

    public static String toString(FlightSolution plan) {
        //        Collection<FlightLeg> anchor = (Collection<FlightLeg>) Collections2
        //                .filter(plan.getProblemFacts(), new Predicate<Object>() {
        //
        //                    @Override
        //                    public boolean apply(Object input) {
        //                        if (input instanceof FlightLeg) {
        //                            return ((FlightLeg) input).previousLeg == null;
        //                        }
        //                        return false;
        //                    }
        //                });
        StringBuilder sb = new StringBuilder();
        for (FlightLeg item : plan.startLegs) {
            FlightLeg start = item;
            int seq = 1;
            while (start != null) {
                sb.append((start.changed() ? "！" : "") + "#" + seq++)
                        .append(" ").append(start).append("\n");
                start = start.nextLeg;
            }
        }
        return sb.toString();
    }

    static Set<LocalDate> dates(String from, String to) {
        LocalDate date = new LocalDate(from);
        LocalDate dateTo = new LocalDate(to);

        Set<LocalDate> set = new HashSet<LocalDate>(10);
        while (date.compareTo(dateTo) <= 0) {
            set.add(date);
            date = date.plusDays(1);
        }
        return set;
    }

    static Set<LocalTime> clocks() {
        LocalTime time = new LocalTime(0, 0);
        LocalTime to = new LocalTime(23, 59);

        Set<LocalTime> set = new HashSet<LocalTime>(10);
        set.add(time);
        while (time.compareTo(to) < 0) {
            time = time.plusMinutes(1);
            set.add(time);
        }
        return set;
    }

    static Set<Weather> weathers = new HashSet<Weather>(10);

    public static Weather weather(Airport airport, Effect affect, String from,
            String to) {
        Weather bean = new Weather(airport, new DateTime(from),
                new DateTime(to), affect);
        weathers.add(bean);
        return bean;
    }

    public static String toString(List<FlightLeg> legs) {
        StringBuilder sb = new StringBuilder();
        for (FlightLeg item : legs) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
