package cn.devit.planner;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

public class ArrivalTimeUpdatingVariableListener
        implements VariableListener<FlightLeg> {

    public void beforeEntityAdded(ScoreDirector scoreDirector,
            FlightLeg flight) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector,
            FlightLeg flight) {
        updateArrivalTime(scoreDirector, flight);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector,
            FlightLeg flight) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector,
            FlightLeg flight) {
        updateArrivalTime(scoreDirector, flight);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector,
            FlightLeg flight) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector,
            FlightLeg flight) {
        // Do nothing
    }

    protected void updateArrivalTime(ScoreDirector scoreDirector,
            FlightLeg flight) {

        FlightLeg prevLeg = flight.previousLeg;
        DateTime arrivalDateTime;
        if (prevLeg == null || prevLeg.plane == null) {
            //飞机飞行的第一个航段，用自己的到港机场时间算作到港时间。
            arrivalDateTime = flight.getArrivalDateTime();
        } else {
            //前一个航段的到港时间就是离岗机场的到港时间。
            arrivalDateTime = prevLeg.getArrivalDateTime();
        }
        //查查自己这段的飞行时间。
        FlightLeg leg = flight;

        Duration flyTime = flight.schedule.getFlyTime();
        //        Duration flyTime = Main2.scheduleFlightTimeTable.row(leg.getLeg())
        //                .get(flight.plane);
        //        if (flyTime == null) {
        //            flyTime = Main2.flightModelTimeTable.row(leg.getLeg())
        //                    .get(flight.plane.model);
        //        }
        //
        //        if (flyTime == null) {
        //            Main2.flightTimeTable.row(leg.getLeg()).get(flight.plane);
        //        }
        //
        //        if (flyTime == null) {
        //            System.out.println("没有找到飞行时间：" + flight.plane + " " + leg);
        //            flyTime = Duration.standardDays(1);
        //        }

        while (flight != null) {
            scoreDirector.beforeVariableChanged(flight, "flyTime");
            flight.flyTime = flyTime;
            scoreDirector.afterVariableChanged(flight, "flyTime");
            arrivalDateTime = flight.getArrivalDateTime();

            flight = flight.nextLeg;
            if (flight != null) {
                flight.departureAirportArrivalTime = arrivalDateTime;
            }
        }
    }
}
