package cselp.domain.report;


import cselp.domain.local.Crew;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PersonLeg implements Serializable {

    private Long personId;
    private Long legId;
    private Long osFlightId;
    private Long flightId;
    private Long legFKId;
    private String carrier;
    private String flightNo;
    private Timestamp flightDate;
    private Timestamp actualDate;
    private Timestamp departure;
    private Timestamp arrival;
    private String origin;
    private String originIcao;
    private String originIata;
    private String destination;
    private String destinationIcao;
    private String destinationIata;
    private String tailNo;
    private String flightRole;
    private Integer low;
    private Integer medium;
    private Integer high;
    private Double score;
    private List<Crew> crews = new ArrayList<>();

    public Long getLegFKId() {
        return legFKId;
    }

    public void setLegFKId(Long legFKId) {
        this.legFKId = legFKId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getLegId() {
        return legId;
    }

    public void setLegId(Long legId) {
        this.legId = legId;
    }

    public Long getOsFlightId() {
        return osFlightId;
    }

    public void setOsFlightId(Long osFlightId) {
        this.osFlightId = osFlightId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public Timestamp getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Timestamp flightDate) {
        this.flightDate = flightDate;
    }

    public Timestamp getActualDate() {
        return actualDate;
    }

    public void setActualDate(Timestamp actualDate) {
        this.actualDate = actualDate;
    }

    public Timestamp getDeparture() {
        return departure;
    }

    public void setDeparture(Timestamp departure) {
        this.departure = departure;
    }

    public Timestamp getArrival() {
        return arrival;
    }

    public void setArrival(Timestamp arrival) {
        this.arrival = arrival;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginIcao() {
        return originIcao;
    }

    public void setOriginIcao(String originIcao) {
        this.originIcao = originIcao;
    }

    public String getOriginIata() {
        return originIata;
    }

    public void setOriginIata(String originIata) {
        this.originIata = originIata;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationIcao() {
        return destinationIcao;
    }

    public void setDestinationIcao(String destinationIcao) {
        this.destinationIcao = destinationIcao;
    }

    public String getDestinationIata() {
        return destinationIata;
    }

    public void setDestinationIata(String destinationIata) {
        this.destinationIata = destinationIata;
    }

    public String getTailNo() {
        return tailNo;
    }

    public void setTailNo(String tailNo) {
        this.tailNo = tailNo;
    }

    public String getFlightRole() {
        return flightRole;
    }

    public void setFlightRole(String flightRole) {
        this.flightRole = flightRole;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getMedium() {
        return medium;
    }

    public void setMedium(Integer medium) {
        this.medium = medium;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<Crew> getCrews() {
        return crews;
    }

    public void setCrews(List<Crew> crews) {
        this.crews = crews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonLeg personLeg = (PersonLeg) o;

        if (personId != null ? !personId.equals(personLeg.personId) : personLeg.personId != null) return false;
        return !(legId != null ? !legId.equals(personLeg.legId) : personLeg.legId != null);

    }

    @Override
    public int hashCode() {
        int result = personId != null ? personId.hashCode() : 0;
        result = 31 * result + (legId != null ? legId.hashCode() : 0);
        return result;
    }
}
