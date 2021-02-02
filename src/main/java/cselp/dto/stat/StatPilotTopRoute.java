package cselp.dto.stat;

import java.io.Serializable;

/**
 * top route info container
 */
public class StatPilotTopRoute implements Serializable {
    // airport codes
    private String dep_airport;
    private String arr_airport;
    // number of flights
    private Integer flights;
    // is it A->B or A->B->A,
    // NOT USED NOW
    private Boolean returning;

    public StatPilotTopRoute() {
    }

    public StatPilotTopRoute(String dep_airport, String arr_airport, Integer flights) {
        this.dep_airport = dep_airport;
        this.arr_airport = arr_airport;
        this.flights = flights;
    }

    public String getDep_airport() {
        return dep_airport;
    }

    public void setDep_airport(String dep_airport) {
        this.dep_airport = dep_airport;
    }

    public String getArr_airport() {
        return arr_airport;
    }

    public void setArr_airport(String arr_airport) {
        this.arr_airport = arr_airport;
    }

    public Integer getFlights() {
        return flights;
    }

    public void setFlights(Integer flights) {
        this.flights = flights;
    }

    public Boolean getReturning() {
        return returning;
    }

    public void setReturning(Boolean returning) {
        this.returning = returning;
    }
}
