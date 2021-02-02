package cselp.dto.stat;

/**
 * Quantity of events by severity for any specific phase of a flight
 */
public class StatPhaseEventsQty extends StatEventsQty {
    private String phase;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
}
