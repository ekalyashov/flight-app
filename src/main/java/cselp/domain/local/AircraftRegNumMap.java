package cselp.domain.local;


import java.io.Serializable;

public class AircraftRegNumMap implements Serializable {

    private String regNum;
    private String alternateRegNum;
    private Short priority;

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getAlternateRegNum() {
        return alternateRegNum;
    }

    public void setAlternateRegNum(String alternateRegNum) {
        this.alternateRegNum = alternateRegNum;
    }

    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AircraftRegNumMap that = (AircraftRegNumMap) o;

        if (!regNum.equals(that.regNum)) return false;
        return alternateRegNum.equals(that.alternateRegNum);

    }

    @Override
    public int hashCode() {
        int result = regNum.hashCode();
        result = 31 * result + alternateRegNum.hashCode();
        return result;
    }
}
