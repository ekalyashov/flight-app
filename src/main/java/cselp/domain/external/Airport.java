package cselp.domain.external;


import java.io.Serializable;

public class Airport implements Serializable {

    private Long id;
    private String icaoCode;
    private String iataCode;
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean userModified;
    private Double magVar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getUserModified() {
        return userModified;
    }

    public void setUserModified(Boolean userModified) {
        this.userModified = userModified;
    }

    public Double getMagVar() {
        return magVar;
    }

    public void setMagVar(Double magVar) {
        this.magVar = magVar;
    }
}
