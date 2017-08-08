package cn.devit.planner;

import static org.junit.Assert.*;

import java.net.URL;

import org.drools.core.io.impl.ClassPathResource;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;


public class RuleTest {

    @Test
    public void test1() throws Exception {

        URL resource = getClass().getResource("debug.drl");
        System.out.println(resource);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("debug.drl", getClass()),
                ResourceType.DRL);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        KieSession kSession = kbase.newKieSession();

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
                f2.schedule.getDepartureDateTime())
                        .getStandardMinutes();
        f2.stayMinutes = 45;
        
        
        
        

        kSession.insert(f1);
        kSession.insert(f2);
        kSession.fireAllRules();
    }
}
