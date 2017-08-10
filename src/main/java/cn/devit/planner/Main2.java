package cn.devit.planner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.drools.core.io.impl.ClassPathResource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;

import com.google.common.io.ByteStreams;

public class Main2 {

    static File file = new File("doc/厦航大赛数据_飞机30.xlsx");

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

        importSolution.write(plan, new File("doc/result.csv"));
        runScore();
        debug(solver, plan);

    }

    final static int LV_HARD = 0;
    final int LV_SOFT = 1;

    static void debug(Solver<FlightSolution> solver, FlightSolution plan) {
        Log.d("===违规项目调试===");
        DroolsScoreDirector scoreDirector = (DroolsScoreDirector) solver
                .getScoreDirectorFactory().buildScoreDirector();
        scoreDirector.setWorkingSolution(plan);
        scoreDirector.calculateScore();
        Collection<ConstraintMatchTotal> inv = scoreDirector
                .getConstraintMatchTotals();
        for (ConstraintMatchTotal item : inv) {
            if (item.getScoreLevel() == LV_HARD) {
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

    static void debug(FlightSolution plan) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("debug.drl", Main2.class),
                ResourceType.DRL);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        KieSession kSession = kbase.newKieSession();
        for (Object item : plan.getProblemFacts()) {
            kSession.insert(item);
        }
        for (Object item : plan.flights) {
            kSession.insert(item);
        }

        int count = kSession.fireAllRules();
        System.out.println("触发规则数量" + count);
    }

    public static String toString(FlightSolution plan) {
        int count = 1;
        StringBuilder sb = new StringBuilder();
        for (FlightLeg item : plan.startLegs) {
            FlightLeg start = item;
            int seq = 1;
            while (start != null) {
                sb.append((start.changed() ? "!" : " "))
                        .append(String.format("%4d#", count++))
                        .append(String.format("%4d ", seq++)).append(start)
                        .append("\n");
                start = start.nextLeg;
            }
        }
        return sb.toString();
    }
}
