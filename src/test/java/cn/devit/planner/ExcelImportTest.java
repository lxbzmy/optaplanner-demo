package cn.devit.planner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class ExcelImportTest {

    @Test
    public void 飞机停留时间是正数() throws Exception {

        ExcelImport excelImport = new ExcelImport();
        excelImport.importExcel();

        Set<Integer> set = new TreeSet<Integer>();

        for (FlightLeg flightLeg : excelImport.toPlan) {

            int stayMinutesBeforeDeparture = flightLeg
                    .getStayMinutesBeforeDeparture();
            set.add(stayMinutesBeforeDeparture);

            assertThat(flightLeg.getDepartureDateTime(),
                    equalTo(flightLeg.schedule.getDepartureDateTime()));
            assertThat(flightLeg.getArrivalDateTime(),equalTo(flightLeg.schedule.arriavalDateTime()));
            assertThat(stayMinutesBeforeDeparture, greaterThanOrEqualTo(0));

        }
    }
}
