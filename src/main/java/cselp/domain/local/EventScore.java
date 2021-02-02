package cselp.domain.local;


import java.io.Serializable;
import java.sql.Timestamp;

public class EventScore implements Serializable {

    private Long eventId;
    private Long eventTypeId;
    private String eventTypeName;
    private Long flightId;
    private Long airportIdTakeOff;
    private Long airportIdLanding;
    private Long aircraftId;
    private Short phaseId;
    private String phaseCode;
    private Short severityId;
    private Integer score;
    private String primaryParameterName;
    private Double value;
    private Timestamp eventTime;
    private Timestamp flightDate;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getAirportIdTakeOff() {
        return airportIdTakeOff;
    }

    public void setAirportIdTakeOff(Long airportIdTakeOff) {
        this.airportIdTakeOff = airportIdTakeOff;
    }

    public Long getAirportIdLanding() {
        return airportIdLanding;
    }

    public void setAirportIdLanding(Long airportIdLanding) {
        this.airportIdLanding = airportIdLanding;
    }

    public Long getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }

    public Short getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Short phaseId) {
        this.phaseId = phaseId;
    }

    public String getPhaseCode() {
        return phaseCode;
    }

    public void setPhaseCode(String phaseCode) {
        this.phaseCode = phaseCode;
    }

    public Short getSeverityId() {
        return severityId;
    }

    public void setSeverityId(Short severityId) {
        this.severityId = severityId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getPrimaryParameterName() {
        return primaryParameterName;
    }

    public void setPrimaryParameterName(String primaryParameterName) {
        this.primaryParameterName = primaryParameterName;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public Timestamp getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Timestamp flightDate) {
        this.flightDate = flightDate;
    }
}
