package cn.devit.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import cn.devit.planner.Edge.EdgeType;
import cn.devit.planner.constraints.AirportCloseTime;
import cn.devit.planner.constraints.PlaneLegConstraint;
import cn.devit.planner.constraints.Weather;
import cn.devit.planner.domain.AnchorPoint;
import cn.devit.planner.domain.FlyTime;

@PlanningSolution()
public class FlightSolution implements Solution<Score<HardSoftScore>> {

    @ValueRangeProvider(id = "plane")
    List<Plane> planes;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "changeableLegs")
    List<FlightLeg> flights;

    
    @ValueRangeProvider(id = "startPoint")
    List<AnchorPoint> anchors;
    
    /**
     * 左边界航段，和startLegs内容一样
     */
    Edge left = new Edge(EdgeType.left);
    /**
     * 保存右边界航段
     */
    Edge right = new Edge(EdgeType.right);
    
    Collection<Weather> weathers;

    @ValueRangeProvider(id = "duration")
    public CountableValueRange<Integer> getMinutesRange() {
        return ValueRangeFactory.createIntValueRange(15, 60 * 24 * 3,5);
    }

    List<Leg> legs = new ArrayList<Leg>();
    List<PlaneLegConstraint> planeLegConstraints = new ArrayList<PlaneLegConstraint>();

    Score<HardSoftScore> score;

    ArrayList<AirportCloseTime> airportCloseTime = new ArrayList<AirportCloseTime>();

    public List<UnScheduleFlight> unScheduleList;

    public List<FlyTime> flyTime = new ArrayList<>();

    @Override
    public Score<HardSoftScore> getScore() {
        return score;
    }

    @Override
    public void setScore(Score<HardSoftScore> score) {
        this.score = score;
    }

    @Override
    public Collection<? extends Object> getProblemFacts() {
        Set<Object> set = new HashSet<Object>(10);
        //实时
        set.addAll(legs);
        set.addAll(planes);
        //限制条件，天气，机场关闭
        set.addAll(airportCloseTime);
        set.addAll(weathers);
        
        //
        set.addAll(flyTime);

        //变量
        //日期，时间
//        set.addAll(getMinutesRange());
        //飞机，航行
        set.addAll(planeLegConstraints);
        //初始锚点
        set.addAll(anchors);
        set.add(left);
        set.add(right);
        return set;

    }

}
