package cselp.domain.local;


import java.io.Serializable;

public class CrewMember implements Serializable {

    private Long crewId;
    private Long personId;
    private String flightRole;

    public Long getCrewId() {
        return crewId;
    }

    public void setCrewId(Long crewId) {
        this.crewId = crewId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getFlightRole() {
        return flightRole;
    }

    public void setFlightRole(String flightRole) {
        this.flightRole = flightRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrewMember that = (CrewMember) o;

        if (crewId != null ? !crewId.equals(that.crewId) : that.crewId != null) return false;
        return !(personId != null ? !personId.equals(that.personId) : that.personId != null);

    }

    @Override
    public int hashCode() {
        int result = crewId != null ? crewId.hashCode() : 0;
        result = 31 * result + (personId != null ? personId.hashCode() : 0);
        return result;
    }
}
