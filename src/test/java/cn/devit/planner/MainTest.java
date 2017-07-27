package cn.devit.planner;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import com.google.common.io.ByteStreams;

public class MainTest {

    @Test
    public void 每分钟() throws Exception {
        Set<LocalTime> clocks = Main.clocks();
        System.out.println(clocks);
    }

    @Test
    public void 天数() throws Exception {
        Set<LocalDate> dates = Main.dates("2017-10-10", "2017-10-11");
        System.out.println(dates);
    }

    @Test
    public void score() throws Exception {
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
}
