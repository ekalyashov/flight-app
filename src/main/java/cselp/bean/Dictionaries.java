package cselp.bean;

import cselp.domain.external.Aircraft;
import cselp.domain.external.Phase;
import cselp.domain.local.AirportCode;
import cselp.domain.local.Squadron;

import java.util.Map;

/**
 * Container for common reference maps.
 */
public class Dictionaries {
    // registration (tail) number -> Aircraft map
    private Map<String, Aircraft> tailAircraftMap;
    // squadron id -> Squadron map
    private Map<Long, Squadron> squadronMap;
    // phase id -> Phase map
    private Map<Short, Phase> phaseMap;
    // AirportCode.getCodes() -> AirportCode map
    private Map<String, AirportCode> aptCodesMap;

    public Map<String, Aircraft> getTailAircraftMap() {
        return tailAircraftMap;
    }

    public void setTailAircraftMap(Map<String, Aircraft> tailAircraftMap) {
        this.tailAircraftMap = tailAircraftMap;
    }

    public Map<Long, Squadron> getSquadronMap() {
        return squadronMap;
    }

    public void setSquadronMap(Map<Long, Squadron> squadronMap) {
        this.squadronMap = squadronMap;
    }

    public Map<Short, Phase> getPhaseMap() {
        return phaseMap;
    }

    public void setPhaseMap(Map<Short, Phase> phaseMap) {
        this.phaseMap = phaseMap;
    }

    public Map<String, AirportCode> getAptCodesMap() {
        return aptCodesMap;
    }

    public void setAptCodesMap(Map<String, AirportCode> aptCodesMap) {
        this.aptCodesMap = aptCodesMap;
    }
}
