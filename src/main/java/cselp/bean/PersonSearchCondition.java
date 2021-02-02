package cselp.bean;

public class PersonSearchCondition {

    private Long divisionId;
    private Long squadronId;
    private String divisionRole;
    private String flightRole;
    private String lastName;

    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }

    public Long getSquadronId() {
        return squadronId;
    }

    public void setSquadronId(Long squadronId) {
        this.squadronId = squadronId;
    }

    public String getDivisionRole() {
        return divisionRole;
    }

    public void setDivisionRole(String divisionRole) {
        this.divisionRole = divisionRole;
    }

    public String getFlightRole() {
        return flightRole;
    }

    public void setFlightRole(String flightRole) {
        this.flightRole = flightRole;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
