package cselp.dto.stat;

import java.io.Serializable;

/**
 * top crew member info container
 */
public class StatPilotTopCrew implements Serializable {
    // person name - who was crew member in one crew with specified Pilot
    private String name;
    // number of flights
    private Integer flights;

    public StatPilotTopCrew() {
    }

    public StatPilotTopCrew(String name, Integer flights) {
        this.name = name;
        this.flights = flights;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFlights() {
        return flights;
    }

    public void setFlights(Integer flights) {
        this.flights = flights;
    }
}
