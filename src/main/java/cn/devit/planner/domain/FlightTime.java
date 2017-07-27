package cn.devit.planner.domain;

import org.joda.time.Duration;

import cn.devit.planner.Leg;
import cn.devit.planner.Plane;

/**
 * 飞机在飞哪个航节的时候用多少时间。
 * <p>
 *
 *
 * @author lxb
 *
 */
public final class FlightTime {

    Plane plane;

    Leg leg;
    /**
     * 落地时间-起飞时间=飞行时间，单位是ms
     */
    Duration duration;
    
    public Duration getDuration() {
        return duration;
    }

    public FlightTime(Plane plane, Leg leg, Duration duration) {
        super();
        this.plane = plane;
        this.leg = leg;
        this.duration = duration;
    }
}
