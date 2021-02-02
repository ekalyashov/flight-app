package cselp.domain.report;


import cselp.domain.local.EventScore;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class PersonFlight implements Serializable {

    private Long personId;
    private Long legId;
    private Long osFlightId;
    private Long flightId;
    private Date date;
    private Time time;
    private String flight;
    private String origin;
    private String dest;
    private String plane;
    private String tailNo;
    private String role;
    private List<EventScore> scores = new ArrayList<>();
    private Double score;
    private Short hiEvents;
    private Short medEvents;
    private Short lowEvents;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getLegId() {
        return legId;
    }

    public void setLegId(Long legId) {
        this.legId = legId;
    }

    public Long getOsFlightId() {
        return osFlightId;
    }

    public void setOsFlightId(Long osFlightId) {
        this.osFlightId = osFlightId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getPlane() {
        return plane;
    }

    public void setPlane(String plane) {
        this.plane = plane;
    }

    public String getTailNo() {
        return tailNo;
    }

    public void setTailNo(String tailNo) {
        this.tailNo = tailNo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<EventScore> getScores() {
        return scores;
    }

    public void setScores(List<EventScore> scores) {
        this.scores = scores;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Short getHiEvents() {
        return hiEvents;
    }

    public void setHiEvents(Short hiEvents) {
        this.hiEvents = hiEvents;
    }

    public Short getMedEvents() {
        return medEvents;
    }

    public void setMedEvents(Short medEvents) {
        this.medEvents = medEvents;
    }

    public Short getLowEvents() {
        return lowEvents;
    }

    public void setLowEvents(Short lowEvents) {
        this.lowEvents = lowEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonFlight that = (PersonFlight) o;

        if (personId != null ? !personId.equals(that.personId) : that.personId != null) return false;
        return !(legId != null ? !legId.equals(that.legId) : that.legId != null);

    }

    @Override
    public int hashCode() {
        int result = personId != null ? personId.hashCode() : 0;
        result = 31 * result + (legId != null ? legId.hashCode() : 0);
        return result;
    }
}
