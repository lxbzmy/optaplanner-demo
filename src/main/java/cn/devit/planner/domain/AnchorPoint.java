package cn.devit.planner.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import cn.devit.planner.Entity;
import cn.devit.planner.FlightLeg;
import cn.devit.planner.Plane;

/**
 * optaplanner 要求链式计划的锚点类型不能和planning entity类型一样。
 * <p>
 *
 *
 * @author lxb
 *
 */
@PlanningEntity
public abstract class AnchorPoint extends Entity {

    @InverseRelationShadowVariable(sourceVariableName = "previousLeg")
    FlightLeg nextFlight;

    public FlightLeg getNextFlight() {
        return nextFlight;
    }
    
    public void setNextFlight(FlightLeg nextFlight) {
        this.nextFlight = nextFlight;
    }

    /**
     * @return 锚点
     */
    public abstract AnchorPoint getPlane();
}
