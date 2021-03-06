package cn.devit.planner;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.Duration;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import com.csvreader.CsvWriter;
import com.google.common.collect.Table.Cell;

import cn.devit.planner.constraints.AirportCloseTime;
import cn.devit.planner.constraints.PlaneLegConstraint;
import cn.devit.planner.constraints.Weather;
import cn.devit.planner.domain.AnchorPoint;
import cn.devit.planner.domain.Cancel;
import cn.devit.planner.domain.FlyTime;

public class ImportSolution implements SolutionFileIO<FlightSolution> {

    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public String getOutputFileExtension() {
        return "csv";
    }

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

        plan.flights = new ArrayList<FlightLeg>(excelImport.toPlan);
        System.out.println("飞行段：" + plan.flights.size());
        
        ArrayList<AnchorPoint> anchors = new ArrayList<AnchorPoint>();
        for (FlightLeg item : excelImport.left) {
            anchors.add(item.plane);
        }
        anchors.add(new Cancel("XX"));
        plan.anchors = anchors;

        System.out.println("起始点：" + (excelImport.left.size()));
        plan.left = excelImport.left;
        plan.right = excelImport.right;
        
        //空飞的飞行时间
        
        
        
        List<FlyTime> list = new ArrayList<FlyTime>();
        Set<Cell<Leg, String, Duration>> cellSet = excelImport.flightTimeTable.cellSet();
        for (Cell<Leg, String, Duration> cell : cellSet) {
            list.add(new FlyTime(cell.getColumnKey(), cell.getRowKey(), cell.getValue()));
        }
        plan.flyTime = list;
        return plan;

    }

    @Override
    public void write(FlightSolution solution, File arg1) {

        FlightSolution plan = (FlightSolution) solution;
        List<FlightLeg> result = new ArrayList<FlightLeg>();
        result.addAll(plan.flights);
        //数据从2364结束，我从2365开始
        int seq = 2365;
        CsvWriter csvWriter = new CsvWriter(arg1.getAbsolutePath());

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        for (FlightLeg item : result) {
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
        
        List<UnScheduleFlight> list = new ArrayList<UnScheduleFlight>();
        if(plan.unScheduleList!=null) {
            list.addAll(plan.unScheduleList);
        }
        int num = 9001;
        for (UnScheduleFlight f : list) {
            String[] content = new String[9];
            content[0] = String.valueOf(num++);
            content[1] = f.leg.departure.id;
            content[2] = f.leg.arrival.id;
            content[3] = format.format(f.departureDateTime.toDate());

            content[4] = format.format(f.arrivalDateTime.toDate());
            content[5] = f.plane.id;
            //            content[6] = resultFlight.isCancel() ? "1" : "0";
            //            content[7] = resultFlight.isStraighten() ? "1" : "0";
            //            content[8] = resultFlight.isEmptyFly() ? "1" : "0";
            content[6] = "0";
            content[7] = "0";
            content[8] = "1";

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
