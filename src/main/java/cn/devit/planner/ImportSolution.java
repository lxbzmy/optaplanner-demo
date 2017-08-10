package cn.devit.planner;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import com.csvreader.CsvWriter;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import cn.devit.planner.constraints.AirportCloseTime;
import cn.devit.planner.constraints.Weather;

public class ImportSolution implements SolutionFileIO<FlightSolution> {

    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public String getOutputFileExtension() {
        return "csv";
    }

    static Predicate<FlightLeg> filter = new Predicate<FlightLeg>() {

        /*
         * 有1,2,3,4,5
         * 1 很容易（很容易就会移动到取消段）
         * 2 难（
         * 3 很容易
         * 4 容易
         * 5 很容易
         * 
         */
        Pattern p = Pattern.compile("1|3|5");

        @Override
        public boolean apply(FlightLeg input) {
            return p.matcher(input.schedule.plane.model).matches();
        }

        @Override
        public String toString() {
            return p.toString();
        }
    };

    @Override
    public FlightSolution read(File file) {

        ExcelImport excelImport = new ExcelImport(file);
        try {
            excelImport.importExcel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        plan.left = excelImport.left;
        plan.right = excelImport.right;

        return plan;

    }

    @Override
    public void write(FlightSolution solution, File arg1) {

        FlightSolution plan = (FlightSolution) solution;
        List<FlightLeg> result = new ArrayList<FlightLeg>();
        result.addAll(plan.startLegs);
        result.addAll(plan.flights);
        //数据从2364结束，我从2365开始
        int seq = 2365;
        CsvWriter csvWriter = new CsvWriter(arg1.getAbsolutePath());

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        for (FlightLeg item : result) {
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
    }

}
