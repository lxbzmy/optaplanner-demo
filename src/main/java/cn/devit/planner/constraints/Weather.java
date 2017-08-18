package cn.devit.planner.constraints;

import org.joda.time.DateTime;

import cn.devit.planner.Airport;

/**
 * 天气控制，坏的天气会导致不能起飞，降落，停机。
 * <p>
 *
 *
 * @author lxb
 *
 */
public final class Weather {

    Airport airport;
    DateTime form;
    DateTime thru;
    Effect affect;
    
    public Weather() {
    }

    public Weather(Airport airport, DateTime form, DateTime thru,
            Effect affect) {
        super();
        this.airport = airport;
        this.form = form;
        this.thru = thru;
        this.affect = affect;
    }

    /**
     * @return the airport
     */
    public Airport getAirport() {
        return airport;
    }

    /**
     * @param airport the airport to set
     */
    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    /**
     * @return the form
     */
    public DateTime getForm() {
        return form;
    }

    /**
     * @param form the form to set
     */
    public void setForm(DateTime form) {
        this.form = form;
    }

    /**
     * @return the thru
     */
    public DateTime getThru() {
        return thru;
    }

    /**
     * @param thru the thru to set
     */
    public void setThru(DateTime thru) {
        this.thru = thru;
    }

    /**
     * @return the affect
     */
    public Effect getAffect() {
        return affect;
    }

    /**
     * @param affect the affect to set
     */
    public void setAffect(Effect affect) {
        this.affect = affect;
    }

    @Override
    public String toString() {
        return "💨(" + airport + " " + affect + " " + form + " " + thru + ")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((affect == null) ? 0 : affect.hashCode());
        result = prime * result + ((airport == null) ? 0 : airport.hashCode());
        result = prime * result + ((form == null) ? 0 : form.hashCode());
        result = prime * result + ((thru == null) ? 0 : thru.hashCode());
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
        Weather other = (Weather) obj;
        if (affect != other.affect)
            return false;
        if (airport == null) {
            if (other.airport != null)
                return false;
        } else if (!airport.equals(other.airport))
            return false;
        if (form == null) {
            if (other.form != null)
                return false;
        } else if (!form.equals(other.form))
            return false;
        if (thru == null) {
            if (other.thru != null)
                return false;
        } else if (!thru.equals(other.thru))
            return false;
        return true;
    }

    
}
