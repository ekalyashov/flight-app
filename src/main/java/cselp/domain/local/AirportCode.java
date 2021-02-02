package cselp.domain.local;

import java.io.Serializable;


public class AirportCode implements Serializable {
    // object identifier
    private Long id;
    // ICAO code
    private String icaoCode;
    // IATA code
    private String iataCode;
    // ICAO code in cyrillic transcription
    private String icaoCyrillicCode;
    // cyrillic 3-letters abbreviature
    private String crtCode;
    //timezone shift in minutes
    private Long aptZone;

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

    public String getIcaoCyrillicCode() {
        return icaoCyrillicCode;
    }

    public void setIcaoCyrillicCode(String icaoCyrillicCode) {
        this.icaoCyrillicCode = icaoCyrillicCode;
    }

    public String getCrtCode() {
        return crtCode;
    }

    public void setCrtCode(String crtCode) {
        this.crtCode = crtCode;
    }

    public Long getAptZone() {
        return aptZone;
    }

    public void setAptZone(Long aptZone) {
        this.aptZone = aptZone;
    }

    public boolean isCodesEquals(AirportCode o) {
        if (this == o) return true;
        if (o == null) return false;

        if (icaoCode != null ? !icaoCode.equals(o.icaoCode) : o.icaoCode != null) return false;
        if (iataCode != null ? !iataCode.equals(o.iataCode) : o.iataCode != null) return false;
        if (icaoCyrillicCode != null ? !icaoCyrillicCode.equals(o.icaoCyrillicCode) : o.icaoCyrillicCode != null)
            return false;
        return !(crtCode != null ? !crtCode.equals(o.crtCode) : o.crtCode != null);
    }

    /**
     * Returns unique identifier for Airport
     * @return unique identifier for Airport
     */
    public String getCodes() {
        return "" + iataCode + ',' + crtCode + ',' + icaoCode + ',' + icaoCyrillicCode;
    }
}
