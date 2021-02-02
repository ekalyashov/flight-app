package cselp.dto;


import java.io.Serializable;

public class User implements Serializable {

    private Long id;
    private Long squadron_id;
    //default value, additional definition required
    private String role = "pilot";
    private String tab_num;
    private String squadron;
    private String division;
    //names
    private String first;
    private String last;
    private String full;
    private String division_role;
    private PilotMinimums mins;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSquadron_id() {
        return squadron_id;
    }

    public void setSquadron_id(Long squadron_id) {
        this.squadron_id = squadron_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getDivision_role() {
        return division_role;
    }

    public void setDivision_role(String division_role) {
        this.division_role = division_role;
    }

    public PilotMinimums getMins() {
        return mins;
    }

    public void setMins(PilotMinimums mins) {
        this.mins = mins;
    }
}
