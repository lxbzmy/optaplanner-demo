package cn.devit.planner.domain;

import org.joda.time.Duration;

import cn.devit.planner.Leg;

/**
 * 空飞的飞行时间
 * <p>
 *
 *
 * @author lxb
 *
 */
public class FlyTime {

    String model;

    Leg leg;
    /**
     * 落地时间-起飞时间=飞行时间，单位是ms
     */
    Duration duration;
    public FlyTime(String model, Leg leg, Duration duration) {
        super();
        this.model = model;
        this.leg = leg;
        this.duration = duration;
    }
 
    public Leg getLeg() {
        return leg;
    }
    
    public String getModel() {
        return model;
    }
    
}
