package cn.devit.planner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import cn.devit.planner.Edge.EdgeType;
import cn.devit.planner.constraints.AirportCloseTime;
import cn.devit.planner.constraints.Effect;
import cn.devit.planner.constraints.Weather;

public class ExcelImport {

    public ExcelImport(File filename) {
        this.file = filename;
    }

    public ExcelImport() {
        this(new File("doc/厦航大赛数据20170705_1.xlsx"));
    }

    File file;

    Set<FlightLeg> toPlan = new HashSet<FlightLeg>();
    Set<FlightLeg> startPoints = new HashSet<FlightLeg>();
    Edge left = new Edge(EdgeType.left);
    Edge right = new Edge(EdgeType.right);

    /**
     * @throws Exception
     */
    @Test
    public void importExcel() throws Exception {
        XSSFWorkbook book = new XSSFWorkbook(file);

        XSSFSheet sheet = book.getSheet("航班");
        //        XSSFRow row = sheet.getRow(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        /*
         * 航班ID 日期  国际/国内   航班号 起飞机场    降落机场    起飞时间    降落时间    飞机ID    机型  重要系数
         * 
         */
        int 航班ID = 0;
        int 日期 = 1;
        int 国际_国内 = 2;
        int 航班号 = 3;
        int 起飞机场 = 4;
        int 降落机场 = 5;
        int 起飞时间 = 6;
        int 降落时间 = 7;
        int 飞机ID = 8;
        int 机型 = 9;
        int 重要系数 = 10;

        List<FlightLeg> list = new ArrayList<FlightLeg>(10);
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();
            FlightLeg flight = new FlightLeg();
            flight.id = row.getCell(航班ID).getRawValue();
            String abroad = row.getCell(国际_国内).getStringCellValue();
            if ("国际".equals(abroad)) {
                flight.crossBorder = true;
            } else if ("国内".equals(abroad)) {
                flight.crossBorder = false;
            } else {
                throw new RuntimeException("没有实现");
            }
            flight.weight = row.getCell(重要系数).getNumericCellValue();

            FlightSchedule2 sc = new FlightSchedule2();
            sc.flightNumber = row.getCell(航班号).getRawValue();
            sc.leg = leg(row.getCell(起飞机场).getRawValue(),
                    row.getCell(降落机场).getRawValue());
            sc.plane = plane(row.getCell(飞机ID).getRawValue(),
                    row.getCell(机型).getRawValue());
            DateTime departure = new DateTime(
                    row.getCell(起飞时间).getDateCellValue());
            sc.departureDate = departure.toLocalDate();
            sc.departureTime = departure.toLocalTime();
            DateTime arrival = new DateTime(
                    row.getCell(降落时间).getDateCellValue());
            sc.arriavalDate = arrival.toLocalDate();
            sc.arriavalTime = arrival.toLocalTime();
            flight.reset(sc);
            list.add(flight);
            scheduleFlightTimeTable.row(sc.leg).put(sc.plane, sc.getFlyTime());
            flightModelTimeTable.row(sc.leg).put(sc.plane.model,
                    sc.getFlyTime());
        }
        //        System.out.println("航班总数：" + list.size());

        ImmutableListMultimap<Plane, FlightLeg> legsGroupByPlane = Multimaps
                .index(list, new Function<FlightLeg, Plane>() {
                    @Override
                    public Plane apply(FlightLeg input) {
                        return input.plane;
                    }
                });

        ImmutableMultiset<Plane> keys = legsGroupByPlane.keys();
        for (Plane plane : keys) {
            List<FlightLeg> sorted = new ArrayList<FlightLeg>(
                    legsGroupByPlane.get(plane));
            Collections.sort(sorted, new Comparator<FlightLeg>() {
                @Override
                public int compare(FlightLeg o1, FlightLeg o2) {
                    return o1.getDepartureDateTime()
                            .compareTo(o2.getDepartureDateTime());
                }
            });
            Iterator<FlightLeg> iterator = sorted.iterator();
            FlightLeg prev = null;

            FlightLeg flight = iterator.next();
            flight.previousLeg = prev;

            startPoints.add(flight);
            //每架飞机的左边界航段
            left.add(flight);

            prev = flight;
            while (iterator.hasNext()) {
                flight = iterator.next();
                flight.previousLeg = prev;
                flight.departureAirportArrivalTime = prev.getArrivalDateTime();
                flight.stayMinutes = (int) new Duration(
                        flight.departureAirportArrivalTime,
                        flight.schedule.getDepartureDateTime())
                                .getStandardMinutes();
                prev.nextLeg = flight;
                prev = flight;
                toPlan.add(flight);
            }
            //每架飞机的右边界航段
            right.add(prev);
        }
        System.out.println("开始点：" + startPoints.size());
        System.out.println("可以安排的段：" + toPlan.size());
        System.out.println("左边界");
        for (FlightLeg item : left) {
            System.out.println(item);
        }
        System.out.println("右边界");
        for (FlightLeg item : right) {
            System.out.println(item);
        }

        航线飞机限制(book);
        机场关闭限制(book);
        台风场景(book);
        飞行时间(book);
        book.close();
    }

    /**
     * 表一中，计划好的飞行时间。
     */
    static Table<Leg, Plane, Duration> scheduleFlightTimeTable = Tables
            .newCustomTable(Maps.<Leg, Map<Plane, Duration>> newLinkedHashMap(),
                    new Supplier<Map<Plane, Duration>>() {
                        public Map<Plane, Duration> get() {
                            return Maps.newLinkedHashMap();
                        }
                    });
    /**
     * 表一中，按照机型查询的时间表
     */
    static Table<Leg, String, Duration> flightModelTimeTable = Tables
            .newCustomTable(
                    Maps.<Leg, Map<String, Duration>> newLinkedHashMap(),
                    new Supplier<Map<String, Duration>>() {
                        public Map<String, Duration> get() {
                            return Maps.newLinkedHashMap();
                        }
                    });

    /**
     * 给调飞机用的飞行时间表。
     */
    static Table<Leg, String, Duration> flightTimeTable = Tables.newCustomTable(
            Maps.<Leg, Map<String, Duration>> newLinkedHashMap(),
            new Supplier<Map<String, Duration>>() {
                public Map<String, Duration> get() {
                    return Maps.newLinkedHashMap();
                }
            });

    private void 飞行时间(XSSFWorkbook book) {
        /*
         * 飞行时间
         * 飞机机型 起飞机场    降落机场    飞行时间（分钟）
         */
        XSSFSheet sheet = book.getSheet("飞行时间");

        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        int 飞机机型 = 0;
        int 起飞机场 = 1;
        int 降落机场 = 2;
        int 飞行时间 = 3;
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();
            Leg leg = leg(row.getCell(起飞机场).getRawValue(),
                    row.getCell(降落机场).getRawValue());
            String mod = row.getCell(飞机机型).getRawValue();
            row.getCell(飞行时间).getRawValue();
            Duration minutes = Duration.standardMinutes(
                    (long) row.getCell(飞行时间).getNumericCellValue());
            //            System.out.println("⌛" + leg + " " + mod + " "
            //                    + minutes.getStandardMinutes() + "分钟");
            flightTimeTable.row(leg).put(mod, minutes);
        }

    }

    List<Weather> weathers = new ArrayList<Weather>();

    private List<Weather> 台风场景(XSSFWorkbook book) {
        /*
         * 台风场景
         * 开始时间 结束时间    影响类型    机场  航班ID    飞机
         */
        XSSFSheet sheet = book.getSheet("台风场景");
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        int 开始时间 = 0;
        int 结束时间 = 1;
        int 影响类型 = 2;
        int 机场 = 3;
        int 航班ID = 4;
        int 飞机 = 5;

        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();
            Airport airport = airport(row.getCell(机场).getRawValue());
            String f = row.getCell(影响类型).getStringCellValue();
            Effect e = Effect.departure;
            if ("降落".equals(f)) {
                e = Effect.arrival;
            } else if ("起飞".equals(f)) {
                e = Effect.departure;
            } else if ("停机".equals(f)) {
                e = Effect.parking;
            } else {
                throw new RuntimeException("没有实现" + f);
            }
            DateTime time1 = new DateTime(row.getCell(开始时间).getDateCellValue());
            DateTime time2 = new DateTime(row.getCell(结束时间).getDateCellValue());
            Weather weather = new Weather(airport, time1, time2, e);
            //            System.out.println(weather);
            weathers.add(weather);
        }
        return weathers;
    }

    List<AirportCloseTime> airportCloseTime = new ArrayList<AirportCloseTime>(
            10);

    private List<AirportCloseTime> 机场关闭限制(XSSFWorkbook book) {
        /*
         * 机场   关闭时间    开放时间    生效日期    失效日期
         * 5   0:01    6:30    1/1/2014    31/12/2017
         */
        XSSFSheet sheet = book.getSheet("机场关闭限制");
        int 机场 = 0;
        int 关闭时间 = 1;
        int 开放时间 = 2;
        int 生效日期 = 3;
        int 失效日期 = 4;
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();

        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();
            Airport airport = airport(row.getCell(机场).getRawValue());
            //            DateTime formDate = new DateTime(row.getCell(生效日期))
            LocalTime closeTime = LocalTime
                    .fromDateFields(row.getCell(关闭时间).getDateCellValue());
            LocalDate formDate = LocalDate
                    .fromDateFields(row.getCell(生效日期).getDateCellValue());
            LocalTime openTime = LocalTime
                    .fromDateFields(row.getCell(开放时间).getDateCellValue());
            LocalDate thruDate = LocalDate
                    .fromDateFields(row.getCell(失效日期).getDateCellValue());
            AirportCloseTime close = new AirportCloseTime(airport, closeTime,
                    openTime, formDate, thruDate);

            airportCloseTime.add(close);
            //            System.out.println(close);
        }
        System.out.println("机场关闭时间数量" + airportCloseTime.size());
        return airportCloseTime;
    }

    List<PlaneLegConstraint> PlaneLegConstraints = new ArrayList<PlaneLegConstraint>(
            10);

    List<PlaneLegConstraint> 航线飞机限制(XSSFWorkbook book) {
        //航线-飞机限制 起飞机场 降落机场    飞机ID
        XSSFSheet sheet = book.getSheet("航线-飞机限制");
        int 起飞机场 = 0;
        int 降落机场 = 1;
        int 飞机ID = 2;
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();//skip title line.
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();
            String planeId = row.getCell(飞机ID).getRawValue();
            Plane plane = plane(planeId);
            Leg leg = leg(row.getCell(起飞机场).getRawValue(),
                    row.getCell(降落机场).getRawValue());
            PlaneLegConstraints.add(new PlaneLegConstraint(plane, leg));
        }
        for (PlaneLegConstraint item : PlaneLegConstraints) {
            //            System.out.println(item);
        }
        System.out.println("飞机航节限制数量" + PlaneLegConstraints.size());
        return PlaneLegConstraints;
    }

    /*
     * ========
     * 数据构造器
     * ========
     */

    Map<String, Airport> airports = new HashMap<String, Airport>();

    Airport airport(String id) {
        Airport port = airports.get(id);
        if (port == null) {
            port = new Airport(id);
            airports.put(id, port);
        }
        return port;
    }

    Map<String, Leg> legs = new HashMap<String, Leg>();

    private Leg leg(String from, String to) {
        String key = from + "-" + to;
        Leg leg = legs.get(key);
        if (leg == null) {
            leg = new Leg(airport(from), airport(to));
            legs.put(key, leg);
        }
        return leg;
    }

    Map<String, Plane> planes = new HashMap<String, Plane>();

    Plane plane(String id, String model) {
        Plane plane = plane(id);
        plane.setModel(model);
        return plane;
    }

    private Plane plane(String planeId) {
        //JAVA8可以改进
        Plane p = planes.get(planeId);
        if (p == null) {
            p = new Plane(planeId);
            planes.put(planeId, p);
        }
        return p;
    }

}
