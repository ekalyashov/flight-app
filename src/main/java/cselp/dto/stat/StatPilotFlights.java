package cselp.dto.stat;

import cselp.dto.Flight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Statistics of flights performed by a pilot
 */
public class StatPilotFlights implements Serializable {
    // total qty of flights starting from the 1st flight performed
    private Integer total;
    // qty of flights in this calendar year
    private Integer year;
    // qty of flights in this calendar month
    private Integer month;
    // qty of flights for last 12 months, NOT including the current one,
    // order is [latest month, ... , newest month],
    private int[] history;
    // qty of average flights performed by a squadron member for last 12 months,
    // NOT including the current one, order is [latest month, ... , newest month],
    private int[] history_sqd_avg;
    // qty of departures for specific time diapasons:
    // [0..6) [6..12) [12..18) [18..24). values calculated for last 3 months
    //now calculated for the year.
    private int[] departures;
    // histogram of durations of flights performed for the last 3 months.
    //now calculated for the year.
    // values are in minutes:   [0..30) [30..60) ___ [330..360) [360+..]
    private int[] durations;
    // header data for the last 5 flights
    private List<Flight> flights = new ArrayList<>();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public int[] getHistory() {
        return history;
    }

    public void setHistory(int[] history) {
        this.history = history;
    }

    public int[] getHistory_sqd_avg() {
        return history_sqd_avg;
    }

    public void setHistory_sqd_avg(int[] history_sqd_avg) {
        this.history_sqd_avg = history_sqd_avg;
    }

    public int[] getDepartures() {
        return departures;
    }

    public void setDepartures(int[] departures) {
        this.departures = departures;
    }

    public int[] getDurations() {
        return durations;
    }

    public void setDurations(int[] durations) {
        this.durations = durations;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
