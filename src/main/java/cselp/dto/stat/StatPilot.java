package cselp.dto.stat;

import java.io.Serializable;

public class StatPilot implements Serializable {

    private StatPilotFlights flights;
    private StatPilotEvents events;
    private StatPilotScores scores;
    private StatPilotTops tops;

    public StatPilotFlights getFlights() {
        return flights;
    }

    public void setFlights(StatPilotFlights flights) {
        this.flights = flights;
    }

    public StatPilotEvents getEvents() {
        return events;
    }

    public void setEvents(StatPilotEvents events) {
        this.events = events;
    }

    public StatPilotScores getScores() {
        return scores;
    }

    public void setScores(StatPilotScores scores) {
        this.scores = scores;
    }

    public StatPilotTops getTops() {
        return tops;
    }

    public void setTops(StatPilotTops tops) {
        this.tops = tops;
    }
}
