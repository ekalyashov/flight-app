package cselp.dto;

import java.util.List;

/**
 * Bean contains results of flights search.
 */
public class FlightSearchResult {
    //count of flights in result
    private int found;
    //total count of flights found in storage
    private long total;
    //offset for first result, selected from storage
    private int first;
    //list of flights
    private List<Flight> flights;

    public FlightSearchResult() {
    }

    public FlightSearchResult(int found, List<Flight> flights) {
        this.found = found;
        this.flights = flights;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
