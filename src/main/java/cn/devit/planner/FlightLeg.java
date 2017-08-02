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
@PlanningEntity()
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

    @PlanningVariable(valueRangeProviderRefs = { "date" })
    LocalDate departureDate;

    @PlanningVariable(valueRangeProviderRefs = { "time" })
    LocalTime departureTime;

    /*
     * 到达时间根据飞机和航线表计算出来。
     */
    @CustomShadowVariable(variableListenerClass = ArrivalTimeUpdatingVariableListener.class, sources = {
            @CustomShadowVariable.Source(variableName = "previousLeg") })
    Duration flyTime;
    DateTime departureAirportArrivalTime;

    FlightLeg previousLeg;

    @PlanningVariable(valueRangeProviderRefs = { "changeableLegs",
            "startPoint" }, graphType = PlanningVariableGraphType.CHAINED)
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
        if (departureDate != null) {
            return new DateTime().withDate(departureDate)
                    .withTime(departureTime);
        }
        return null;
    }

    /**
     * 航段
     * 
     */
    public DateTime getArrivalDateTime() {
        return getDepartureDateTime().plus(flyTime);
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
        return sc.plane + " "
                + (sc.departureDate.equals(departureDate) ? ""
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
                    + (crossBorder ? "国际" : "国内") + weight + " "
                    + schedule.departureDate + "，原计划：" + toString(schedule);
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
        this.departureDate = sc.departureDate;
        this.departureTime = sc.departureTime;
    }

    public boolean changed() {
        if (schedule != null && schedule.plane != null
                && schedule.plane.equals(this.plane)
                && schedule.departureDate.equals(this.departureDate)
                && schedule.departureTime.equals(this.departureTime)) {
            return false;
        }
        return true;
    }
    
    public int getStayMinutesBeforeDeparture() {
        if (previousLeg != null) {
            return (int) new Duration(getDepartureAirportArrivalTime(),
                    getDepartureDateTime()).getStandardMinutes();
        }
        return Integer.MAX_VALUE;
    }

    public DateTime getDepartureAirportArrivalTime() {
        return departureAirportArrivalTime;
    }

}
