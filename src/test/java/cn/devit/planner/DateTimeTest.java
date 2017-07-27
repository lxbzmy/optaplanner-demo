package cn.devit.planner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

public class DateTimeTest {

    @Test
    public void duration() throws Exception {
        DateTime date1 = new DateTime("2017-01-01");
        DateTime date2 = new DateTime("2017-01-02");
        Duration d = new Duration(date1, date2);
        //        PT86400S
        //        System.out.println(d.toString());
        assertThat(d.getStandardDays(), is(1L));

        d = new Duration(date2, date1);
        //        PT-86400S
        assertThat(d.getStandardDays(), is(-1L));
        System.out.println(d.toString());

    }

    @Test
    public void testName() throws Exception {

    }
}
