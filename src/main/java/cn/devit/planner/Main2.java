package cn.devit.planner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.google.common.io.ByteStreams;

import cn.devit.planner.domain.AnchorPoint;

public class Main2 {

    static File file = new File("doc/厦航大赛数据_飞机XX.xlsx");

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // Build the Solver
        SolverFactory<FlightSolution> solverFactory = SolverFactory
                .createFromXmlInputStream(
                        Main2.class.getResourceAsStream("flight.xml"));
        Solver<FlightSolution> solver = solverFactory.buildSolver();

        ImportSolution importSolution = new ImportSolution();
        FlightSolution plan = (FlightSolution) importSolution.read(file);

        debug(solver, plan);

        solver.solve(plan);
        plan = solver.getBestSolution();

        // Display the result
        System.out.println("\nResult:\n");
        System.out.println(toString(plan));
        List<UnScheduleFlight> list = 填补空飞(solver,plan);
        plan.unScheduleList = list;
        importSolution.write(plan, new File("doc/result.csv"));
        runScore();
        debug(solver, plan);

    }

    final static int LV_HARD = 0;
    final int LV_SOFT = 1;

    static List<UnScheduleFlight> 填补空飞(Solver<FlightSolution> solver, FlightSolution plan) {
        
        List<UnScheduleFlight> list = new ArrayList<UnScheduleFlight>();
        
        ScoreDirector<FlightSolution> scoreDirector = solver
                .getScoreDirectorFactory().buildScoreDirector();
        scoreDirector.setWorkingSolution(plan);
        scoreDirector.calculateScore();
        Collection<ConstraintMatchTotal> inv = scoreDirector
                .getConstraintMatchTotals();
        for (ConstraintMatchTotal item : inv) {
            if ("需要调机".equals(item.getConstraintName())) {
                Set<? extends ConstraintMatch> set = item
                        .getConstraintMatchSet();
                for (ConstraintMatch ii : set) {
                    List<Object> justificationList = ii.getJustificationList();
                    FlightLeg left = (FlightLeg) justificationList.get(0);
                    FlightLeg right = (FlightLeg) justificationList.get(1);
                    if(left.getNextFlight() == right) {
                        
                    }else {
                        left = right;
                        right = left.getNextFlight();
                    }
                    Leg leg = new Leg(left.getLeg().getArrival(),right.getLeg().getDeparture());
                    DateTime dep = new DateTime(left.getArrivalDateTime().plusMinutes(50));
                    Map<String, Duration> row = ExcelImport.flightTimeTable.row(leg);
                    if(row.containsKey(left.plane.model)) {
                        Duration duration = row.get(left.plane.model);
                        
                        UnScheduleFlight flight = new UnScheduleFlight();
                        flight.plane =  left.plane;
                        flight.departureDateTime = dep;
                        flight.leg = leg;
                        flight.arrivalDateTime = dep.plus(duration);
                        flight.emptyFlight  = true;
                        list.add(flight);
                        System.out.println(flight);
                    }else {
                        Log.d("航段无法调机直飞：{},{}",left,right);
                    }
                   
                }
            }
        }
        return list;
    }

    static void debug(Solver<FlightSolution> solver, FlightSolution plan) {
        Log.d("===违规项目调试===");
        ScoreDirector<FlightSolution> scoreDirector = solver
                .getScoreDirectorFactory().buildScoreDirector();
        scoreDirector.setWorkingSolution(plan);
        Score score = scoreDirector.calculateScore();
        Log.d("分数{}", score);
        Collection<ConstraintMatchTotal> inv = scoreDirector
                .getConstraintMatchTotals();
        for (ConstraintMatchTotal item : inv) {
            HardSoftScore s = (HardSoftScore) item.getScoreTotal();
            if (s.getHardScore() != 0) {
                Log.d("项目：" + item.getConstraintName());

                Set<? extends ConstraintMatch> vo = item
                        .getConstraintMatchSet();
                int i = 1;
                for (ConstraintMatch match : vo) {
                    Log.d("#{}", i++);
                    List<Object> list = match.getJustificationList();
                    for (Object object : list) {
                        System.out.println(object);
                    }

                }
            }
        }
        System.out.println("================");
    }

    static void runScore() {
        try {
            Process proc = new ProcessBuilder()
                    .directory(file.getParentFile()).command("java", "-jar",
                            "XMAEvaluation.jar", file.getName(), "result.csv")
                    .start();
            InputStream inputStream = proc.getInputStream();
            byte[] byteArray = ByteStreams.toByteArray(inputStream);
            System.out.println(new String(byteArray, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(FlightSolution plan) {
        int count = 1;
        StringBuilder sb = new StringBuilder();
        List<AnchorPoint> anchors = plan.anchors;
        for (AnchorPoint f : anchors) {
            sb.append(f).append("\n");
            FlightLeg start = f.getNextFlight();
            int seq = 1;
            while (start != null) {
                sb.append((start.changed() ? "!" : " "))
                        .append(String.format("%4d#", count++))
                        .append(String.format("%4d ", seq++)).append(start)
                        .append("\n");
                start = start.getNextFlight();
            }
        }
        return sb.toString();
    }
}
