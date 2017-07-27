package cn.devit.rectangle;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import cn.devit.planner.FlightPlan;

@PlanningSolution
public class RectangleFind implements Solution<HardSoftScore> {

    public static void main(String[] args) {
        // Build the Solver
        SolverFactory<RectangleFind> solverFactory = SolverFactory
                .createFromXmlFile(new File(
                        "src/main/resources/cn/devit/rectangle/solver.xml"));
        Solver<RectangleFind> solver = solverFactory.buildSolver();

        RectangleFind problem = new RectangleFind();
        Point home = new Point(0, 0);
        Point rd = new Point(2, 0);
        Point rt = new Point(2, 1);
        Point lt = new Point(0, 1);

        Visit visit1 = new Visit();
        visit1.point = home;
        problem.center = visit1;

        Visit visit2 = new Visit();
        visit2.point = rd;
        Visit visit3 = new Visit();
        visit3.point = rt;
        Visit visit4 = new Visit();
        visit4.point = lt;
        
        visit2.setPrevPoint(visit1);

        List<Visit> list = new ArrayList<Visit>();
        list.add(visit2);
        list.add(visit3);
        list.add(visit4);
        problem.visits = list;

        RectangleFind solve = solver.solve(problem);

        System.out.println("结果：");
        for (Object item : solve.getVisits()) {
            System.out.println(item);

        }
        //           Visit s = visit1;
        //           while(s.from!=null){
        //               System.out.println(s.point+"-"+s.from);
        //               s = s.from;
        //           }

    }

    private HardSoftScore score;

    private Visit center;

    private List<Visit> visits;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "visits")
    public List<Visit> getVisits() {
        return new ArrayList<Visit>(visits);
    }

    @ValueRangeProvider(id = "home")
    public Set<Visit> getCenter() {
        return Collections.singleton(center);
    }

    @Override
    public HardSoftScore getScore() {
        return score;
    }

    @Override
    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    @Override
    public Collection<? extends Object> getProblemFacts() {

        Set<Object> set = new HashSet<Object>(10);
        set.addAll(visits);
        set.add(center);
        set.add(center.point);
        return set;

    }

}
