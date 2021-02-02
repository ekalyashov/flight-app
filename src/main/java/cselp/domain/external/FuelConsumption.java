package cselp.domain.external;


import java.io.Serializable;
import java.sql.Timestamp;

public class FuelConsumption implements Serializable {
    private Long id;
    private Short engineNum;
    private Double engineConsumption1;
    private Double engineConsumption2;
    private Double engineConsumption3;
    private Double engineConsumption4;
    private Double apuConsumption;
    private Timestamp engineRuntime1;
    private Timestamp engineRuntime2;
    private Timestamp engineRuntime3;
    private Timestamp engineRuntime4;
    private Timestamp apuRuntime;
    private Timestamp engineAntiIceTime1;
    private Timestamp engineAntiIceTime2;
    private Timestamp engineAntiIceTime3;
    private Timestamp engineAntiIceTime4;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getEngineNum() {
        return engineNum;
    }

    public void setEngineNum(Short engineNum) {
        this.engineNum = engineNum;
    }

    public Double getEngineConsumption1() {
        return engineConsumption1;
    }

    public void setEngineConsumption1(Double engineConsumption1) {
        this.engineConsumption1 = engineConsumption1;
    }

    public Double getEngineConsumption2() {
        return engineConsumption2;
    }

    public void setEngineConsumption2(Double engineConsumption2) {
        this.engineConsumption2 = engineConsumption2;
    }

    public Double getEngineConsumption3() {
        return engineConsumption3;
    }

    public void setEngineConsumption3(Double engineConsumption3) {
        this.engineConsumption3 = engineConsumption3;
    }

    public Double getEngineConsumption4() {
        return engineConsumption4;
    }

    public void setEngineConsumption4(Double engineConsumption4) {
        this.engineConsumption4 = engineConsumption4;
    }

    public Double getApuConsumption() {
        return apuConsumption;
    }

    public void setApuConsumption(Double apuConsumption) {
        this.apuConsumption = apuConsumption;
    }

    public Timestamp getEngineRuntime1() {
        return engineRuntime1;
    }

    public void setEngineRuntime1(Timestamp engineRuntime1) {
        this.engineRuntime1 = engineRuntime1;
    }

    public Timestamp getEngineRuntime2() {
        return engineRuntime2;
    }

    public void setEngineRuntime2(Timestamp engineRuntime2) {
        this.engineRuntime2 = engineRuntime2;
    }

    public Timestamp getEngineRuntime3() {
        return engineRuntime3;
    }

    public void setEngineRuntime3(Timestamp engineRuntime3) {
        this.engineRuntime3 = engineRuntime3;
    }

    public Timestamp getEngineRuntime4() {
        return engineRuntime4;
    }

    public void setEngineRuntime4(Timestamp engineRuntime4) {
        this.engineRuntime4 = engineRuntime4;
    }

    public Timestamp getApuRuntime() {
        return apuRuntime;
    }

    public void setApuRuntime(Timestamp apuRuntime) {
        this.apuRuntime = apuRuntime;
    }

    public Timestamp getEngineAntiIceTime1() {
        return engineAntiIceTime1;
    }

    public void setEngineAntiIceTime1(Timestamp engineAntiIceTime1) {
        this.engineAntiIceTime1 = engineAntiIceTime1;
    }

    public Timestamp getEngineAntiIceTime2() {
        return engineAntiIceTime2;
    }

    public void setEngineAntiIceTime2(Timestamp engineAntiIceTime2) {
        this.engineAntiIceTime2 = engineAntiIceTime2;
    }

    public Timestamp getEngineAntiIceTime3() {
        return engineAntiIceTime3;
    }

    public void setEngineAntiIceTime3(Timestamp engineAntiIceTime3) {
        this.engineAntiIceTime3 = engineAntiIceTime3;
    }

    public Timestamp getEngineAntiIceTime4() {
        return engineAntiIceTime4;
    }

    public void setEngineAntiIceTime4(Timestamp engineAntiIceTime4) {
        this.engineAntiIceTime4 = engineAntiIceTime4;
    }
}
