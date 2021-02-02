package cselp.dto;


import java.io.Serializable;

public class FlightEvent implements Serializable {
    // time in seconds(?) relative to the start of the flight
    private long time;
    // name of the the phase of the flight
    private String phase;
    // type of the event
    private String type;
    // severity: 0 - Info, 1 - Low, 2 - Medium, 3 - High
    private int severity;
    // optional score, MUST NOT be populated for (current_user == pilot)
    private Integer score;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
