package cn.devit.planner;


import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import cn.devit.planner.domain.AnchorPoint;

/**
 * 飞机
 * <p>
 *
 *
 * @author lxb
 *
 */
//@PlanningEntity
public class Plane extends AnchorPoint {
    
    private Plane() {
    }

    /**
     * 机型，机型衡量载客能力。
     */
    String model;

    public Plane(String id) {
        setId(id);
    }

    /**
     * 设置飞机型号
     * 
     * @param model
     */
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "✈️[" + id + " M" + model + "]";
    }

    @Override
    public Plane getPlane() {
        return this;
    }
 
    public String getModel() {
        return model;
    }
}
