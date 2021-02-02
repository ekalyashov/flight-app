package cselp.domain.local;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Leg implements Serializable {
    //leg identifier
    private Long id;
    //trip identifier
    private Long tripId;
    //flight identifier
    private Long flightId;
    //aircraft registration number
    private String tailNum;
    //origin, dest - string of airport codes
    private String origin;
    private String destination;
    private String originIcao;
    private String destinationIcao;
    private String originIata;
    private String destinationIata;
    private Timestamp departurePlan;
    private Timestamp departureFact;
    private Timestamp arrivalPlan;
    private Timestamp arrivalFact;
    private Timestamp takeOff;
    private Timestamp touchDown;
    private Timestamp lastLogDate;
    private List<Crew> crews = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
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

    public String getOriginIcao() {
        return originIcao;
    }

    public void setOriginIcao(String originIcao) {
        this.originIcao = originIcao;
    }

    public String getDestinationIcao() {
        return destinationIcao;
    }

    public void setDestinationIcao(String destinationIcao) {
        this.destinationIcao = destinationIcao;
    }

    public String getOriginIata() {
        return originIata;
    }

    public void setOriginIata(String originIata) {
        this.originIata = originIata;
    }

    public String getDestinationIata() {
        return destinationIata;
    }

    public void setDestinationIata(String destinationIata) {
        this.destinationIata = destinationIata;
    }

    public Timestamp getDeparturePlan() {
        return departurePlan;
    }

    public void setDeparturePlan(Timestamp departurePlan) {
        this.departurePlan = departurePlan;
    }

    public Timestamp getDepartureFact() {
        return departureFact;
    }

    public void setDepartureFact(Timestamp departureFact) {
        this.departureFact = departureFact;
    }

    public Timestamp getArrivalPlan() {
        return arrivalPlan;
    }

    public void setArrivalPlan(Timestamp arrivalPlan) {
        this.arrivalPlan = arrivalPlan;
    }

    public Timestamp getArrivalFact() {
        return arrivalFact;
    }

    public void setArrivalFact(Timestamp arrivalFact) {
        this.arrivalFact = arrivalFact;
    }

    public Timestamp getTakeOff() {
        return takeOff;
    }

    public void setTakeOff(Timestamp takeOff) {
        this.takeOff = takeOff;
    }

    public Timestamp getTouchDown() {
        return touchDown;
    }

    public void setTouchDown(Timestamp touchDown) {
        this.touchDown = touchDown;
    }

    public Timestamp getLastLogDate() {
        return lastLogDate;
    }

    public void setLastLogDate(Timestamp lastLogDate) {
        this.lastLogDate = lastLogDate;
    }

    public List<Crew> getCrews() {
        return crews;
    }

    public void setCrews(List<Crew> crews) {
        this.crews = crews;
    }
}
