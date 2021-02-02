package cselp.bean;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirportCodesContainer {

    private Map<String, Long> codesToAptMap = new HashMap<>();
    private Map<String, Long> icaoToAptMap = new HashMap<>();
    private Map<String, List<Long>> iataToAptMap = new HashMap<>();

    public Map<String, Long> getCodesToAptMap() {
        return codesToAptMap;
    }

    public void setCodesToAptMap(Map<String, Long> codesToAptMap) {
        this.codesToAptMap = codesToAptMap;
    }

    public Map<String, Long> getIcaoToAptMap() {
        return icaoToAptMap;
    }

    public void setIcaoToAptMap(Map<String, Long> icaoToAptMap) {
        this.icaoToAptMap = icaoToAptMap;
    }

    public Map<String, List<Long>> getIataToAptMap() {
        return iataToAptMap;
    }

    public void setIataToAptMap(Map<String, List<Long>> iataToAptMap) {
        this.iataToAptMap = iataToAptMap;
    }
}
