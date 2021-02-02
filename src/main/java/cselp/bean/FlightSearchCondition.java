package cselp.bean;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FlightSearchCondition {

    private List<Long> personIds = new ArrayList<>();
    private Timestamp from;
    private Timestamp to;
    private String originIcao;
    private String destinationIcao;
    private String tailNum;
    private String flightRole;
    private List<Short> severity;
    private Integer firstResult;
    private Integer maxResults;
    //true - asc, false - desc
    private Boolean order;

    private boolean utc = false;

    public List<Long> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(List<Long> personIds) {
        this.personIds = personIds;
    }

    public Timestamp getFrom() {
        return from;
    }

    public void setFrom(Timestamp from) {
        this.from = from;
    }

    public Timestamp getTo() {
        return to;
    }

    public void setTo(Timestamp to) {
        this.to = to;
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

    public String getTailNum() {
        return tailNum;
    }

    public void setTailNum(String tailNum) {
        this.tailNum = tailNum;
    }

    public String getFlightRole() {
        return flightRole;
    }

    public void setFlightRole(String flightRole) {
        this.flightRole = flightRole;
    }

    public List<Short> getSeverity() {
        return severity;
    }

    public void setSeverity(List<Short> severity) {
        this.severity = severity;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Boolean getOrder() {
        return order;
    }

    public void setOrder(Boolean order) {
        this.order = order;
    }

    public boolean isUtc() {
        return utc;
    }

    public void setUtc(boolean utc) {
        this.utc = utc;
    }
}
