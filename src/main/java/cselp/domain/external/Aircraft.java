package cselp.domain.external;


import java.io.Serializable;

public class Aircraft implements Serializable {

    private Long id;
    private Long airlineId;
    private Long configurationId;
    private String registrationNum;
    private String shortRegNum;
    private String comments;
    private Airline airline;
    private AircraftConfiguration configuration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }

    public Long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public String getRegistrationNum() {
        return registrationNum;
    }

    public void setRegistrationNum(String registrationNum) {
        this.registrationNum = registrationNum;
    }

    public String getShortRegNum() {
        return shortRegNum;
    }

    public void setShortRegNum(String shortRegNum) {
        this.shortRegNum = shortRegNum;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public AircraftConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AircraftConfiguration configuration) {
        this.configuration = configuration;
    }
}
