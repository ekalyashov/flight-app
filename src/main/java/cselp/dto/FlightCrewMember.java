package cselp.dto;


import java.io.Serializable;

public class FlightCrewMember implements Serializable {
    // personal number, unique identifier
    private String tab_num;
    // names of parent squadron and division, optional(?)
    private String squadron;
    private String division;
    // role on the flight
    private String flight_role;
    // names
    private String first;
    private String last;

    public FlightCrewMember() {
    }

    public FlightCrewMember(String tab_num, String squadron, String division, String flight_role) {
        this.tab_num = tab_num;
        this.squadron = squadron;
        this.division = division;
        this.flight_role = flight_role;
    }

    public String getTab_num() {
        return tab_num;
    }

    public void setTab_num(String tab_num) {
        this.tab_num = tab_num;
    }

    public String getSquadron() {
        return squadron;
    }

    public void setSquadron(String squadron) {
        this.squadron = squadron;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getFlight_role() {
        return flight_role;
    }

    public void setFlight_role(String flight_role) {
        this.flight_role = flight_role;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
