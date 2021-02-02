package cselp.domain.external;


import java.io.Serializable;
import java.sql.Timestamp;

public class Flight implements Serializable {

    private Long id;
    private Long aptIdTakeOff;
    private Long aptIdLanding;
    private Long rwyIdTakeOff;
    private Long rwyIdLanding;
    private Long alnId;
    private Timestamp startDate;
    private Timestamp flightDuration;
    private Timestamp endDate;
    private String number;
    private Long acrId;
    private Long prcId;
    private Long fcpId;
    private Short lowEventCount;
    private Short mediumEventCount;
    private Short highEventCount;
    private Short infoEventCount;
    private String aircraftRegNum;
    private Short statusId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAptIdTakeOff() {
        return aptIdTakeOff;
    }

    public void setAptIdTakeOff(Long aptIdTakeOff) {
        this.aptIdTakeOff = aptIdTakeOff;
    }

    public Long getAptIdLanding() {
        return aptIdLanding;
    }

    public void setAptIdLanding(Long aptIdLanding) {
        this.aptIdLanding = aptIdLanding;
    }

    public Long getRwyIdTakeOff() {
        return rwyIdTakeOff;
    }

    public void setRwyIdTakeOff(Long rwyIdTakeOff) {
        this.rwyIdTakeOff = rwyIdTakeOff;
    }

    public Long getRwyIdLanding() {
        return rwyIdLanding;
    }

    public void setRwyIdLanding(Long rwyIdLanding) {
        this.rwyIdLanding = rwyIdLanding;
    }

    public Long getAlnId() {
        return alnId;
    }

    public void setAlnId(Long alnId) {
        this.alnId = alnId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(Timestamp flightDuration) {
        this.flightDuration = flightDuration;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getAcrId() {
        return acrId;
    }

    public void setAcrId(Long acrId) {
        this.acrId = acrId;
    }

    public Long getPrcId() {
        return prcId;
    }

    public void setPrcId(Long prcId) {
        this.prcId = prcId;
    }

    public Long getFcpId() {
        return fcpId;
    }

    public void setFcpId(Long fcpId) {
        this.fcpId = fcpId;
    }

    public Short getLowEventCount() {
        return lowEventCount;
    }

    public void setLowEventCount(Short lowEventCount) {
        this.lowEventCount = lowEventCount;
    }

    public Short getMediumEventCount() {
        return mediumEventCount;
    }

    public void setMediumEventCount(Short mediumEventCount) {
        this.mediumEventCount = mediumEventCount;
    }

    public Short getHighEventCount() {
        return highEventCount;
    }

    public void setHighEventCount(Short highEventCount) {
        this.highEventCount = highEventCount;
    }

    public Short getInfoEventCount() {
        return infoEventCount;
    }

    public void setInfoEventCount(Short infoEventCount) {
        this.infoEventCount = infoEventCount;
    }

    public String getAircraftRegNum() {
        return aircraftRegNum;
    }

    public void setAircraftRegNum(String aircraftRegNum) {
        this.aircraftRegNum = aircraftRegNum;
    }

    public Short getStatusId() {
        return statusId;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }
}
