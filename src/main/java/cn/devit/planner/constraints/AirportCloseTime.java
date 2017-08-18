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
    
    public Airport getAirport() {
        return airport;
    }
    
    public LocalTime getCloseTime() {
        return closeTime;
    }
    public LocalTime getOpenTime() {
        return openTime;
    }
    public LocalDate getValidFrom() {
        return validFrom;
    }
    public LocalDate getValidThru() {
        return validThru;
    }

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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((airport == null) ? 0 : airport.hashCode());
        result = prime * result
                + ((closeTime == null) ? 0 : closeTime.hashCode());
        result = prime * result
                + ((openTime == null) ? 0 : openTime.hashCode());
        result = prime * result
                + ((validFrom == null) ? 0 : validFrom.hashCode());
        result = prime * result
                + ((validThru == null) ? 0 : validThru.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AirportCloseTime other = (AirportCloseTime) obj;
        if (airport == null) {
            if (other.airport != null)
                return false;
        } else if (!airport.equals(other.airport))
            return false;
        if (closeTime == null) {
            if (other.closeTime != null)
                return false;
        } else if (!closeTime.equals(other.closeTime))
            return false;
        if (openTime == null) {
            if (other.openTime != null)
                return false;
        } else if (!openTime.equals(other.openTime))
            return false;
        if (validFrom == null) {
            if (other.validFrom != null)
                return false;
        } else if (!validFrom.equals(other.validFrom))
            return false;
        if (validThru == null) {
            if (other.validThru != null)
                return false;
        } else if (!validThru.equals(other.validThru))
            return false;
        return true;
    }
    
}
