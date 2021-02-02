package cselp.domain.external;


import java.io.Serializable;
import java.sql.Timestamp;

public class FlightIdentification implements Serializable {
    private Long id;
    private Timestamp flightDate;
    private String fldNumber;
    private Short fldLegNumber;
    private String registrationNumber;
    private Timestamp creationDate;
    private Timestamp expirationDate;
    private Boolean autoExpire;
    private Short usageFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Timestamp flightDate) {
        this.flightDate = flightDate;
    }

    public String getFldNumber() {
        return fldNumber;
    }

    public void setFldNumber(String fldNumber) {
        this.fldNumber = fldNumber;
    }

    public Short getFldLegNumber() {
        return fldLegNumber;
    }

    public void setFldLegNumber(Short fldLegNumber) {
        this.fldLegNumber = fldLegNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getAutoExpire() {
        return autoExpire;
    }

    public void setAutoExpire(Boolean autoExpire) {
        this.autoExpire = autoExpire;
    }

    public Short getUsageFlag() {
        return usageFlag;
    }

    public void setUsageFlag(Short usageFlag) {
        this.usageFlag = usageFlag;
    }
}
