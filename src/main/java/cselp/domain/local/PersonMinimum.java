package cselp.domain.local;

import java.io.Serializable;

public class PersonMinimum implements Serializable {

    private Long personId;
    private Double landingVisVertical;
    private Double landingVisHorizontal;
    private Double takeOffVisHorizontal;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Double getLandingVisVertical() {
        return landingVisVertical;
    }

    public void setLandingVisVertical(Double landingVisVertical) {
        this.landingVisVertical = landingVisVertical;
    }

    public Double getLandingVisHorizontal() {
        return landingVisHorizontal;
    }

    public void setLandingVisHorizontal(Double landingVisHorizontal) {
        this.landingVisHorizontal = landingVisHorizontal;
    }

    public Double getTakeOffVisHorizontal() {
        return takeOffVisHorizontal;
    }

    public void setTakeOffVisHorizontal(Double takeOffVisHorizontal) {
        this.takeOffVisHorizontal = takeOffVisHorizontal;
    }
}
