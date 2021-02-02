package cselp.domain.external;

import java.io.Serializable;
import java.sql.Timestamp;

public class Event implements Serializable {

    private Long id;
    private Long typeId;
    private Short severityId;
    private Long flightId;
    private Short phaseId;
    private Long frameNum;
    private Short statusId;
    private Timestamp eventTime;
    private Long eventTimeMs; //time from flight start, ms

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Short getSeverityId() {
        return severityId;
    }

    public void setSeverityId(Short severityId) {
        this.severityId = severityId;
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

    public Long getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(Long frameNum) {
        this.frameNum = frameNum;
    }

    public Short getStatusId() {
        return statusId;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public Long getEventTimeMs() {
        return eventTimeMs;
    }

    public void setEventTimeMs(Long eventTimeMs) {
        this.eventTimeMs = eventTimeMs;
    }
}
