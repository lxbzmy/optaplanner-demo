package cn.devit.planner;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;

public class Benchmark {

    public static void main(String[] args) {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory
                .createFromFreemarkerXmlInputStream(
                        Benchmark.class.getResourceAsStream("benchmark.xml"));
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory
                .buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }
}
