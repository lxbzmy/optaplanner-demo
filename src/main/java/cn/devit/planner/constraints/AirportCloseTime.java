package cn.devit.planner.constraints;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import cn.devit.planner.Airport;

/**
 * 机场关闭时间。
 * 在 {@link #validFrom}这天0点开始，到 {@link #validThru}这天最后一秒之间的天数，每天
 * closeTime关闭，直到当天openTime刚过。
 * closeTime < openTime ,比如：每天0:10——6:10关闭，不会出现跨天的情形。
 * <p>
 *
 *
 * @author lxb
 *
 */
public class AirportCloseTime {

    Airport airport;

    LocalTime closeTime;
    LocalTime openTime;

    LocalDate validFrom;
    LocalDate validThru;

    public AirportCloseTime(Airport airport, LocalTime closeTime,
            LocalTime openTime, LocalDate validFrom, LocalDate validThru) {
        super();
        this.airport = airport;
        this.closeTime = closeTime;
        this.openTime = openTime;
        this.validFrom = validFrom;
        this.validThru = validThru;
    }

    @Override
    public String toString() {
        return "关闭(" + airport + "," + closeTime + "-" + openTime + " "
                + validFrom + " " + validThru + ")";
    }
}
