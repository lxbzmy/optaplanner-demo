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

}
