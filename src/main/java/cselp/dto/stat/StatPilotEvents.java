package cselp.dto.stat;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Pilot events statistics
 */
public class StatPilotEvents implements Serializable {
    // total qty of events from the 1st flight
    private StatEventsQty total = new StatEventsQty();
    // qty of events in this calendar year
    private StatEventsQty year = new StatEventsQty();
    // qty of events in this calendar month
    private StatEventsQty month = new StatEventsQty();
    // qty of events for last 12 months, NOT including the current one, order is [latest month, ... , newest month],
    // values could be null to indicate missing info:
    // (history[0] == null) could mean that there were now flights year ago
    private List<StatEventsQty> history = new ArrayList<>();
    // Quantity of events for all known phases, for any time diapason (month, year, ...)
    //calculated for year ago from current date
    private List<StatPhaseEventsQty> by_phase = new ArrayList<>();

    public StatEventsQty getTotal() {
        return total;
    }

    public void setTotal(StatEventsQty total) {
        this.total = total;
    }

    public StatEventsQty getYear() {
        return year;
    }

    public void setYear(StatEventsQty year) {
        this.year = year;
    }

    public StatEventsQty getMonth() {
        return month;
    }

    public void setMonth(StatEventsQty month) {
        this.month = month;
    }

    public List<StatEventsQty> getHistory() {
        return history;
    }

    public void setHistory(List<StatEventsQty> history) {
        this.history = history;
    }

    public List<StatPhaseEventsQty> getBy_phase() {
        return by_phase;
    }

    public void setBy_phase(List<StatPhaseEventsQty> by_phase) {
        this.by_phase = by_phase;
    }
}
