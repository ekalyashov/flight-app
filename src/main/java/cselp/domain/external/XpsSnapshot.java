package cselp.domain.external;

import java.io.Serializable;


public class XpsSnapshot implements Serializable {

    private Long eventId;
    private Long parameterId;
    private Double value;
    private Double valueMinus1;
    private Double valueMinus2;
    private Double valueMinus3;
    private Double valuePlus1;
    private Double valuePlus2;
    private Double valuePlus3;
    private String validity;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValueMinus1() {
        return valueMinus1;
    }

    public void setValueMinus1(Double valueMinus1) {
        this.valueMinus1 = valueMinus1;
    }

    public Double getValueMinus2() {
        return valueMinus2;
    }

    public void setValueMinus2(Double valueMinus2) {
        this.valueMinus2 = valueMinus2;
    }

    public Double getValueMinus3() {
        return valueMinus3;
    }

    public void setValueMinus3(Double valueMinus3) {
        this.valueMinus3 = valueMinus3;
    }

    public Double getValuePlus1() {
        return valuePlus1;
    }

    public void setValuePlus1(Double valuePlus1) {
        this.valuePlus1 = valuePlus1;
    }

    public Double getValuePlus2() {
        return valuePlus2;
    }

    public void setValuePlus2(Double valuePlus2) {
        this.valuePlus2 = valuePlus2;
    }

    public Double getValuePlus3() {
        return valuePlus3;
    }

    public void setValuePlus3(Double valuePlus3) {
        this.valuePlus3 = valuePlus3;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XpsSnapshot that = (XpsSnapshot) o;
        if (eventId != null ? !eventId.equals(that.eventId) : that.eventId != null) return false;
        return !(parameterId != null ? !parameterId.equals(that.parameterId) : that.parameterId != null);

    }

    @Override
    public int hashCode() {
        int result = eventId != null ? eventId.hashCode() : 0;
        result = 31 * result + (parameterId != null ? parameterId.hashCode() : 0);
        return result;
    }
}
