package cn.devit.planner;

import org.eclipse.jdt.annotation.NonNull;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

/**
 * 航班,按照航节表示的。
 * <p>
 * - 机场对
 * - 执飞飞机
 * - 日期
 * - 起降时间
 *
 * @author lxb
 *
 */
/**
 * 
 * <p>
 *
 *
 * @author lxb
 *
 */
@PlanningEntity(difficultyComparatorClass = FlightLegComparator.class)
public class FlightLeg extends Entity {

    public FlightLeg() {
    }

    public FlightLeg(String id) {
        setId(id);
    }

    public String getFlightNumber() {
        return schedule.flightNumber;
    }

    /**
     * 航班是否跨境（是：国际，否：国内）
     */
    boolean crossBorder = false;

    public boolean isCrossBorder() {
        return crossBorder;
    }

    /**
     * 延时的分钟数
     * 
     * @return 单位，分钟，正表示延误，负表示提前离港。
     */
    public int getDelayMinutes() {
        return (int) new Duration(schedule.getDepartureDateTime(),
                getDepartureDateTime()).getStandardMinutes();
    }

    /**
     * 航班的原始计划，不能为空
     */
    @NonNull
    FlightSchedule2 schedule;

    public FlightSchedule2 getSchedule() {
        return schedule;
    }

    /**
     * 在离港机场停留的时间，如果是第一段，那么停留时间算作INT_MAX.
     */
    @PlanningVariable(valueRangeProviderRefs = {
            "duration" }, strengthComparatorClass = IntegerComparator.class)
    Integer stayMinutes = 0;

    /**
     * 到达离港机场的时间
     */
    @CustomShadowVariable(variableListenerClass = ArrivalTimeUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "previousLeg") })
    DateTime departureAirportArrivalTime;

    Duration flyTime;

    FlightLeg previousLeg;

    @PlanningVariable(valueRangeProviderRefs = { "changeableLegs",
            "startPoint" }, graphType = PlanningVariableGraphType.CHAINED, strengthComparatorClass = FlightLegComparator.class
    //TODO 次排序是否适合放在这里？
    )
    public FlightLeg getPreviousLeg() {
        return previousLeg;
    }

    public void setPreviousLeg(FlightLeg previousLeg) {
        this.previousLeg = previousLeg;
    }

    Plane plane;

    @InverseRelationShadowVariable(sourceVariableName = "previousLeg")
    FlightLeg nextLeg;

    /**
     * 重要系数。
     */
    double weight = 1;

    public Duration getFlyTime() {
        return flyTime;
    }

    public DateTime getDepartureDateTime() {
        return getDepartureAirportArrivalTime().plusMinutes(stayMinutes);
    }

    public LocalDate getDepartureDate() {
        return getDepartureDateTime().toLocalDate();
    }

    public LocalTime getDepartureTime() {
        return getDepartureDateTime().toLocalTime();
    }

    /**
     * 航段
     * 
     */
    public DateTime getArrivalDateTime() {
        return getDepartureDateTime().plus(flyTime);
    }

    public LocalDate getArrivalDate() {
        return getArrivalDateTime().toLocalDate();
    }

    public LocalTime getArrivalTime() {
        return getArrivalDateTime().toLocalTime();
    }

    /**
     * @return the leg
     */
    public Leg getLeg() {
        return schedule.leg;
    }

    /**
     * @return the plane
     */
    @AnchorShadowVariable(sourceVariableName = "previousLeg")
    public Plane getPlane() {
        return plane;
    }

    /**
     * @param plane the plane prevPoint set
     */
    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    String toString(DateTime dateTime) {
        if (dateTime != null) {
            return dateTime.toString("YY-MM-dd HH:mm");
        }
        return null;
    }

    String toString(FlightSchedule2 sc) {
        if (sc == null) {
            return null;
        }
        DateTime departureDateTime = getDepartureDateTime();
        return sc.plane + " "
                + (sc.departureDate.equals(departureDateTime.toLocalDate()) ? ""
                        : sc.departureDate + " ")
                + toString(sc.departureTime) + " " + toString(sc.arriavalTime);
    }

    String toString(LocalTime time) {
        return time.toString("HH:mm");
    }

    @Override
    public String toString() {
        if ("0".equals(id)) {
            return id + "空航段";
        }
        if (plane == null) {
            return id + " 航班号" + getFlightNumber() + " " + getLeg() + " " + "取消"
                    + (crossBorder ? "国际" : "国内") + weight + "，原计划："
                    + toString(schedule);
        }
        return id + " 航班号" + getFlightNumber() + " " + getLeg() + " " + plane
                + (crossBorder ? "国际" : "国内") + weight + " "
                + toString(getDepartureDateTime()) + " TO "
                + (getArrivalDateTime().toLocalDate()
                        .equals(getDepartureDateTime().toLocalDate())
                                ? toString(getArrivalDateTime().toLocalTime())
                                : toString(getArrivalDateTime()))
                + "，原计划：" + toString(schedule);
    }

    /**
     * 用Schedule的内容填充flight。
     * 
     * @param sc
     */
    public void reset(FlightSchedule2 sc) {
        this.schedule = sc;
        this.flyTime = sc.getFlyTime();
        this.plane = sc.plane;
        this.departureAirportArrivalTime = new DateTime()
                .withDate(sc.departureDate).withTime(sc.departureTime);
    }

    public boolean changed() {
        if (schedule != null && schedule.plane != null
                && schedule.plane.equals(this.plane)
                && schedule.getDepartureDateTime()
                        .equals(getDepartureDateTime())) {
            return false;
        }
        return true;
    }

    public int getStayMinutesBeforeDeparture() {
        //        if (previousLeg != null) {
        //            return (int) new Duration(getDepartureDateTime(),
        //                    getDepartureAirportArrivalTime()).getStandardMinutes();
        //        }
        //        return Integer.MAX_VALUE;
        return stayMinutes;
    }

    /**
     * @return 离港机场到达的时间，如果没有前续航段了，就取schedule里面的离港时间。
     */
    public DateTime getDepartureAirportArrivalTime() {
        if (departureAirportArrivalTime == null) {
            return schedule.getDepartureDateTime();
        }
        return departureAirportArrivalTime;
    }

}
