package cn.devit.rectangle;

import java.awt.Point;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public class Visit {
    
    public Visit() {
    }

    Point point;

    Visit prevPoint;
    
    public Point getPoint() {
        return point;
    }
    
    @PlanningVariable(valueRangeProviderRefs={"visits","home"},graphType=PlanningVariableGraphType.CHAINED)
    public Visit getPrevPoint() {
        return prevPoint;
    }
    
    public void setPrevPoint(Visit from) {
        this.prevPoint = from;
    }
    
    public double getDistance(){
        return point.distance(prevPoint.point);
    }
    
    @Override
    public String toString() {
        return "["+(prevPoint==null?null:prevPoint.point)+"-"+point+"]";
    }
}
