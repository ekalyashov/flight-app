package cselp.dto;


import cselp.dto.stat.StatEventsQty;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Container to transfer flight information.
 */
public class Flight implements Serializable {
    //flight identifier
    private Long id;
    //departure time
    private Timestamp dep_datetime;
    //arrival time
    private Timestamp arr_datetime;
    // timezone offsets for departure and arrival airports (in hours), default is zero
    private int dep_airport_tzo;
    private int arr_airport_tzo;
    //departure airport
    private String dep_airport;
    //arrival airport
    private String arr_airport;
    // flight number
    private String flight;
    // plane type
    private String plane;
    // tail number
    private String tail;

    // role of the current user
    //flight_role? :number;

    // events by severity in
    private StatEventsQty stat_events;
    // total score of this flight,
    // optional and MUST NOT be populated for (current_user == pilot)
    private Integer stat_score;
    // crew on the flight, o
    private FlightCrew crew;
    // events on the flight
    private List<FlightEvent> events = new ArrayList<>();

    /**
     * default constructor
     */
    public Flight() {
    }

    /**
     * Constructor
     * @param id flight identifier
     * @param dep_datetime departure time
     * @param arr_datetime arrival time
     * @param dep_airport departure airport
     * @param arr_airport arrival airport
     * @param tail tail number
     */
    public Flight(Long id, Timestamp dep_datetime, Timestamp arr_datetime,
                  String dep_airport, String arr_airport, String tail) {
        this.id = id;
        this.dep_datetime = dep_datetime;
        this.arr_datetime = arr_datetime;
        this.dep_airport = dep_airport;
        this.arr_airport = arr_airport;
        this.tail = tail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDep_datetime() {
        return dep_datetime;
    }

    public void setDep_datetime(Timestamp dep_datetime) {
        this.dep_datetime = dep_datetime;
    }

    public Timestamp getArr_datetime() {
        return arr_datetime;
    }

    public void setArr_datetime(Timestamp arr_datetime) {
        this.arr_datetime = arr_datetime;
    }

    public int getDep_airport_tzo() {
        return dep_airport_tzo;
    }

    public void setDep_airport_tzo(int dep_airport_tzo) {
        this.dep_airport_tzo = dep_airport_tzo;
    }

    public int getArr_airport_tzo() {
        return arr_airport_tzo;
    }

    public void setArr_airport_tzo(int arr_airport_tzo) {
        this.arr_airport_tzo = arr_airport_tzo;
    }

    public String getDep_airport() {
        return dep_airport;
    }

    public void setDep_airport(String dep_airport) {
        this.dep_airport = dep_airport;
    }

    public String getArr_airport() {
        return arr_airport;
    }

    public void setArr_airport(String arr_airport) {
        this.arr_airport = arr_airport;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getPlane() {
        return plane;
    }

    public void setPlane(String plane) {
        this.plane = plane;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public StatEventsQty getStat_events() {
        return stat_events;
    }

    public void setStat_events(StatEventsQty stat_events) {
        this.stat_events = stat_events;
    }

    public Integer getStat_score() {
        return stat_score;
    }

    public void setStat_score(Integer stat_score) {
        this.stat_score = stat_score;
    }

    public FlightCrew getCrew() {
        return crew;
    }

    public void setCrew(FlightCrew crew) {
        this.crew = crew;
    }

    public List<FlightEvent> getEvents() {
        return events;
    }

    public void setEvents(List<FlightEvent> events) {
        this.events = events;
    }
}
