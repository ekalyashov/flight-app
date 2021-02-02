package cselp.domain.external;

import java.io.Serializable;
import java.sql.Timestamp;


public class EventPrimaryParameter implements Serializable {

    private Long id;
    private Long eventTypeId;
    private Long flightId;
    private Short phaseId;
    private Short severityId;
    private String primaryParameterName;
    private Double parameterValue;
    private Timestamp eventTime;
    private Timestamp flightTime;
    private Long airportIdTakeOff;
    private Long airportIdLanding;
    private Long aircraftId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Short getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Short phaseId) {
        this.phaseId = phaseId;
    }

    public Short getSeverityId() {
        return severityId;
    }

    public void setSeverityId(Short severityId) {
        this.severityId = severityId;
    }

    public String getPrimaryParameterName() {
        return primaryParameterName;
    }

    public void setPrimaryParameterName(String primaryParameterName) {
        this.primaryParameterName = primaryParameterName;
    }

    public Double getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(Double parameterValue) {
        this.parameterValue = parameterValue;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public Timestamp getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(Timestamp flightTime) {
        this.flightTime = flightTime;
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
}
