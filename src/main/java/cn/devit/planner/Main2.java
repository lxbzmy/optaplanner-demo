package cn.devit.planner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.drools.core.io.impl.ClassPathResource;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.csvreader.CsvWriter;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.io.ByteStreams;

import cn.devit.planner.constraints.AirportCloseTime;
import cn.devit.planner.constraints.Weather;

public class Main2 {

    static Predicate<FlightLeg> filter = new Predicate<FlightLeg>() {
        /*
         * 有1,2,3,4,5
         * 1 容易（很容易就会移动到取消段）
         * 2 难（
         * 3 容易
         * 4 -1
         * 5 容易
         * 
         */
        Pattern p = Pattern.compile("4");

        @Override
        public boolean apply(FlightLeg input) {
            return p.matcher(input.schedule.plane.model).matches();
        }

        @Override
        public String toString() {
            return p.toString();
        }
    };

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ExcelImport excelImport = new ExcelImport();
        excelImport.importExcel();

        // Build the Solver
        SolverFactory<FlightSolution> solverFactory = SolverFactory
                .createFromXmlInputStream(
                        Main2.class.getResourceAsStream("flight.xml"));
        Solver<FlightSolution> solver = solverFactory.buildSolver();

        FlightSolution plan = new FlightSolution();
        plan.legs = new ArrayList<Leg>(excelImport.legs.values());
        plan.planes = new ArrayList<Plane>(excelImport.planes.values());

        plan.weathers = new ArrayList<Weather>(excelImport.weathers);
        plan.planeLegConstraints = new ArrayList<PlaneLegConstraint>(
                excelImport.PlaneLegConstraints);
        plan.airportCloseTime = new ArrayList<AirportCloseTime>(
                excelImport.airportCloseTime);

        System.out.println("筛选条件：" + filter);
        plan.flights = new ArrayList<FlightLeg>(
                Collections2.filter(excelImport.toPlan, filter));
        System.out.println("飞行段：" + plan.flights.size());
        plan.startLegs = new ArrayList<FlightLeg>();
        plan.startLegs.add(new NullFlightLeg());
        plan.startLegs
                .addAll(Collections2.filter(excelImport.startPoints, filter));
        System.out.println("起始点：" + (plan.startLegs.size() - 1));
        
        solver.solve(plan);
        plan = solver.getBestSolution();

        // Display the result
        System.out.println("\nResult:\n");
        System.out.println(toString(plan));

        List<FlightLeg> result = new ArrayList<FlightLeg>();
        result.addAll(plan.startLegs);
        result.addAll(plan.flights);

        saveCsv(result);

        debug(plan);
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

    public static void saveCsv(List<FlightLeg> list) {
        //数据从2364结束，我从2365开始
        int seq = 2365;
        CsvWriter csvWriter = new CsvWriter(("doc/result.csv"));

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        for (FlightLeg item : list) {
            if (item instanceof NullFlightLeg) {
                continue;
            }
            String[] content = new String[9];
            content[0] = item.id;
            content[1] = item.schedule.leg.departure.id;
            content[2] = item.schedule.leg.arrival.id;
            content[3] = format.format(item.getDepartureDateTime().toDate());

            content[4] = format.format(item.getArrivalDateTime().toDate());
            content[5] = item.plane != null ? item.plane.id
                    : item.schedule.plane.id;
            //            content[6] = resultFlight.isCancel() ? "1" : "0";
            //            content[7] = resultFlight.isStraighten() ? "1" : "0";
            //            content[8] = resultFlight.isEmptyFly() ? "1" : "0";
            content[6] = item.plane == null ? "1" : "0";
            content[7] = "0";
            content[8] = "0";

            try {
                csvWriter.writeRecord(content);
            } catch (IOException e) {
                System.out.println("写CSV文件失败");
                e.printStackTrace();
            }
        }
        csvWriter.close();
        runScore();
    }

    static void runScore() {
        try {
            Process proc = new ProcessBuilder().directory(new File("doc"))
                    .command("java", "-jar", "XMAEvaluation.jar",
                            "厦航大赛数据20170705_1.xlsx", "result.csv")
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

    public static String toString(List<FlightLeg> legs) {
        StringBuilder sb = new StringBuilder();
        for (FlightLeg item : legs) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
