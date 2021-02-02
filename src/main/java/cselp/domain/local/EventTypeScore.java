package cselp.domain.local;

import java.io.Serializable;


public class EventTypeScore implements Serializable {
    private Long eventTypeId;
    private Short severityId;
    private Integer score;
    private Double coefficient;

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
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

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventTypeScore that = (EventTypeScore) o;

        if (!eventTypeId.equals(that.eventTypeId)) return false;
        return severityId.equals(that.severityId);

    }

    @Override
    public int hashCode() {
        int result = eventTypeId.hashCode();
        result = 31 * result + severityId.hashCode();
        return result;
    }
}
