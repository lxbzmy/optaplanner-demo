package cn.devit.planner;

/**
 * 航节，机场对。航节的飞行时间是固定的。
 * <p>
 *
 *
 * @author lxb
 *
 */
public class Leg {

    Airport departure;

    Airport arrival;

    public Airport getDeparture() {
        return departure;
    }

    public Airport getArrival() {
        return arrival;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((arrival == null) ? 0 : arrival.hashCode());
        result = prime * result
                + ((departure == null) ? 0 : departure.hashCode());
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
        Leg other = (Leg) obj;
        if (arrival == null) {
            if (other.arrival != null)
                return false;
        } else if (!arrival.equals(other.arrival))
            return false;
        if (departure == null) {
            if (other.departure != null)
                return false;
        } else if (!departure.equals(other.departure))
            return false;
        return true;
    }

    public Leg(String from, String to) {
        this.departure = new Airport(from);
        this.arrival = new Airport(to);
    }

    public Leg(Airport from, Airport to) {
        this.departure = from;
        this.arrival = to;
    }

    @Override
    public String toString() {
        return "[" + departure + "-" + arrival + "]";
    }
}
