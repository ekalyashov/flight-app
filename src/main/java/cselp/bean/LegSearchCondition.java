package cselp.bean;


import cselp.domain.local.Leg;

import java.util.HashSet;
import java.util.Set;

public class LegSearchCondition extends Leg {

    private Set<Long> originIds = new HashSet<>();
    private Set<Long> destinationIds = new HashSet<>();

    public LegSearchCondition() {
    }

    public LegSearchCondition(Leg other) {
        setId(other.getId());
        setTripId(other.getTripId());
        setFlightId(other.getFlightId());
        setTailNum(other.getTailNum());
        setOrigin(other.getOrigin());
        setDestination(other.getDestination());
        setOriginIcao(other.getOriginIcao());
        setDestinationIcao(other.getDestinationIcao());
        setOriginIata(other.getOriginIata());
        setDestinationIata(other.getDestinationIata());
        setDeparturePlan(other.getDeparturePlan());
        setDepartureFact(other.getDepartureFact());
        setArrivalPlan(other.getArrivalPlan());
        setArrivalFact(other.getArrivalFact());
        setTakeOff(other.getTakeOff());
        setTouchDown(other.getTouchDown());
        setLastLogDate(other.getLastLogDate());
    }

    public Set<Long> getOriginIds() {
        return originIds;
    }

    public Set<Long> getDestinationIds() {
        return destinationIds;
    }
}
