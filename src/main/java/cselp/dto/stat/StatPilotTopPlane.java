package cselp.dto.stat;

import java.io.Serializable;

/**
 * top plane info container
 */
public class StatPilotTopPlane implements Serializable {
    // plane type
    private String plane;
    // number of flights
    private Integer flights;

    public StatPilotTopPlane() {
    }

    public StatPilotTopPlane(String plane, Integer flights) {
        this.plane = plane;
        this.flights = flights;
    }

    public String getPlane() {
        return plane;
    }

    public void setPlane(String plane) {
        this.plane = plane;
    }

    public Integer getFlights() {
        return flights;
    }

    public void setFlights(Integer flights) {
        this.flights = flights;
    }
}
