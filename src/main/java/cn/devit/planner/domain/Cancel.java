package cn.devit.planner.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

import cn.devit.planner.Plane;

@PlanningEntity
public final class Cancel extends AnchorPoint {

    @Override
    public Plane getPlane() {
        return null;
    }

    @Override
    public String toString() {
        return "Cancel";
    }
}
