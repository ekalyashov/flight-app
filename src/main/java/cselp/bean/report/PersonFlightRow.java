package cselp.bean.report;


import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class PersonFlightRow implements Serializable {
    private Long personId;
    private Date date;
    private Time time;
    private String flight;
    private String origin;
    private String dest;
    private String plane;
    private String tailNo;
    private String role;

    private double score;
    private short hiEvents;
    private short medEvents;
    private short lowEvents;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public short getHiEvents() {
        return hiEvents;
    }

    public void setHiEvents(short hiEvents) {
        this.hiEvents = hiEvents;
    }

    public short getMedEvents() {
        return medEvents;
    }

    public void setMedEvents(short medEvents) {
        this.medEvents = medEvents;
    }

    public short getLowEvents() {
        return lowEvents;
    }

    public void setLowEvents(short lowEvents) {
        this.lowEvents = lowEvents;
    }
}
