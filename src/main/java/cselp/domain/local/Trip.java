package cselp.domain.local;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable {

    private Long id;
    private String carrier;
    private String flightNum;
    private Timestamp flightDate;
    private Timestamp actualDate;
    //aircraft registration number
    private String tailNum;
    //origin, dest - string of airport codes
    private String origin;
    private String destination;
    private String flightKind;
    private List<Leg> legs = new ArrayList<>();

    public Trip() {
    }

    public Trip(String carrier, String flightNum,
                Timestamp flightDate, Timestamp actualDate, String tailNum,
                String origin, String destination, String flightKind) {
        this.carrier = carrier;
        this.flightNum = flightNum;
        this.flightDate = flightDate;
        this.actualDate = actualDate;
        this.tailNum = tailNum;
        this.origin = origin;
        this.destination = destination;
        this.flightKind = flightKind;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public Timestamp getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Timestamp flightDate) {
        this.flightDate = flightDate;
    }

    public Timestamp getActualDate() {
        return actualDate;
    }

    public void setActualDate(Timestamp actualDate) {
        this.actualDate = actualDate;
    }

    public String getTailNum() {
        return tailNum;
    }

    public void setTailNum(String tailNum) {
        this.tailNum = tailNum;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFlightKind() {
        return flightKind;
    }

    public void setFlightKind(String flightKind) {
        this.flightKind = flightKind;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}
