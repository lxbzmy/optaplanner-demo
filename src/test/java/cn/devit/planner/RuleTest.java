package cn.devit.planner;

import java.net.URL;

import org.drools.core.io.impl.ClassPathResource;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;

import cn.devit.planner.constraints.Effect;
import cn.devit.planner.constraints.Weather;

public class RuleTest {

    @Test
    public void test1() throws Exception {

        KieSession kSession = session();

        FlightLeg f1 = new FlightLeg("1");
        FlightSchedule2 sc = new FlightSchedule2();
        sc.leg = new Leg("48", "50");
        sc.flightNumber = "346";
        sc.plane = new Plane("30");
        sc.plane.model = "1";
        sc.departureDate = new LocalDate("2017-05-05");
        sc.departureTime = new LocalTime("13:20");
        sc.arriavalDate = new LocalDate("2017-05-05");
        sc.arriavalTime = new LocalTime("16:10");
        f1.reset(sc);

        FlightLeg f2 = new FlightLeg("2");
        sc = new FlightSchedule2();
        sc.leg = new Leg("50", "62");
        sc.flightNumber = "499";
        sc.plane = new Plane("30");
        sc.plane.model = "1";
        sc.departureDate = new LocalDate("2017-05-05");
        sc.departureTime = new LocalTime("16:30");
        sc.arriavalDate = new LocalDate("2017-05-05");
        sc.arriavalTime = new LocalTime("17:20");
        f2.reset(sc);

        f1.nextLeg = f2;
        f2.previousLeg = f1;

        f2.departureAirportArrivalTime = f1.getArrivalDateTime();
        f2.stayMinutes = (int) new org.joda.time.Duration(
                f2.departureAirportArrivalTime,
                f2.schedule.getDepartureDateTime()).getStandardMinutes();
        f2.stayMinutes = 45;

        kSession.insert(f1);
        kSession.insert(f2);
        kSession.fireAllRules();
    }

    @Test
    public void 台风离港可以提前6小时() throws Exception {

        KieSession kSession = session();

        FlightLeg f1 = new FlightLeg("1");
        FlightSchedule2 sc = new FlightSchedule2();
        sc.leg = new Leg("48", "50");
        sc.flightNumber = "346";
        sc.plane = new Plane("30");
        sc.plane.model = "1";
        sc.departureDate = new LocalDate("2017-05-05");
        sc.departureTime = new LocalTime("7:20");
        sc.arriavalDate = new LocalDate("2017-05-05");
        sc.arriavalTime = new LocalTime("10:10");
        f1.reset(sc);

        FlightLeg f2 = new FlightLeg("2");
        sc = new FlightSchedule2();
        sc.leg = new Leg("50", "62");
        sc.flightNumber = "499";
        sc.plane = new Plane("30");
        sc.plane.model = "1";
        sc.departureDate = new LocalDate("2017-05-05");
        sc.departureTime = new LocalTime("22:10");
        sc.arriavalDate = new LocalDate("2017-05-05");
        sc.arriavalTime = new LocalTime("23:10");
        f2.reset(sc);

        f1.nextLeg = f2;
        f2.previousLeg = f1;

        f2.departureAirportArrivalTime = f1.getArrivalDateTime();
        f2.stayMinutes = (int) new org.joda.time.Duration(
                f2.departureAirportArrivalTime,
                f2.schedule.getDepartureDateTime()).getStandardMinutes();
        System.out.println("原先停留："+f2.stayMinutes);
        f2.stayMinutes = 500;
        System.out.println("延误："+f2.getDelayMinutes());
        System.out.println("现在离港:"+f2.getDepartureDateTime());
        Weather weather = new Weather(new Airport("50"),new DateTime(2017,5,5,20,0,0),new DateTime(2017,5,5,23,0,0),Effect.departure);
//        现在离港:2017-05-05T11:00:00.000+08:00
        kSession.insert(f1);
        kSession.insert(f2);
        kSession.insert(weather);
        kSession.fireAllRules();
    }

    KieSession session() {
        URL resource = getClass().getResource("debug.drl");
        System.out.println(resource);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("debug.drl", getClass()),
                ResourceType.DRL);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        KieSession kSession = kbase.newKieSession();
        return kSession;
    }
}
