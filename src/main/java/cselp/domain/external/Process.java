package cselp.domain.external;


import java.io.Serializable;
import java.sql.Timestamp;

public class Process implements Serializable {
    private Long id;
    private Long recorderTypeId;
    private Long cartridgeId;
    private Long fapConfId;
    private String fileName;
    private Timestamp date;
    private Timestamp cartridgeDateIn;
    private Timestamp cartridgeDateOut;
    private Short flightLegsCount;
    private Integer framesCount;
    private Integer badFramesCount;
    private Integer subFramesCount;
    private Integer badSubFramesCount;
    private Timestamp transcriptionSpeed;
    private Double errorRate;
    private String archiveFileName;
    private Timestamp fapStartDate;
    private Timestamp fapEndDate;
    private Integer fapFramesCount;
    private Integer fapRejectedFramesCount;
    private String comments;
    private String dataDirectory;
    private Long aircraftId;
    private String login;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecorderTypeId() {
        return recorderTypeId;
    }

    public void setRecorderTypeId(Long recorderTypeId) {
        this.recorderTypeId = recorderTypeId;
    }

    public Long getCartridgeId() {
        return cartridgeId;
    }

    public void setCartridgeId(Long cartridgeId) {
        this.cartridgeId = cartridgeId;
    }

    public Long getFapConfId() {
        return fapConfId;
    }

    public void setFapConfId(Long fapConfId) {
        this.fapConfId = fapConfId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Timestamp getCartridgeDateIn() {
        return cartridgeDateIn;
    }

    public void setCartridgeDateIn(Timestamp cartridgeDateIn) {
        this.cartridgeDateIn = cartridgeDateIn;
    }

    public Timestamp getCartridgeDateOut() {
        return cartridgeDateOut;
    }

    public void setCartridgeDateOut(Timestamp cartridgeDateOut) {
        this.cartridgeDateOut = cartridgeDateOut;
    }

    public Short getFlightLegsCount() {
        return flightLegsCount;
    }

    public void setFlightLegsCount(Short flightLegsCount) {
        this.flightLegsCount = flightLegsCount;
    }

    public Integer getFramesCount() {
        return framesCount;
    }

    public void setFramesCount(Integer framesCount) {
        this.framesCount = framesCount;
    }

    public Integer getBadFramesCount() {
        return badFramesCount;
    }

    public void setBadFramesCount(Integer badFramesCount) {
        this.badFramesCount = badFramesCount;
    }

    public Integer getSubFramesCount() {
        return subFramesCount;
    }

    public void setSubFramesCount(Integer subFramesCount) {
        this.subFramesCount = subFramesCount;
    }

    public Integer getBadSubFramesCount() {
        return badSubFramesCount;
    }

    public void setBadSubFramesCount(Integer badSubFramesCount) {
        this.badSubFramesCount = badSubFramesCount;
    }

    public Timestamp getTranscriptionSpeed() {
        return transcriptionSpeed;
    }

    public void setTranscriptionSpeed(Timestamp transcriptionSpeed) {
        this.transcriptionSpeed = transcriptionSpeed;
    }

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }

    public String getArchiveFileName() {
        return archiveFileName;
    }

    public void setArchiveFileName(String archiveFileName) {
        this.archiveFileName = archiveFileName;
    }

    public Timestamp getFapStartDate() {
        return fapStartDate;
    }

    public void setFapStartDate(Timestamp fapStartDate) {
        this.fapStartDate = fapStartDate;
    }

    public Timestamp getFapEndDate() {
        return fapEndDate;
    }

    public void setFapEndDate(Timestamp fapEndDate) {
        this.fapEndDate = fapEndDate;
    }

    public Integer getFapFramesCount() {
        return fapFramesCount;
    }

    public void setFapFramesCount(Integer fapFramesCount) {
        this.fapFramesCount = fapFramesCount;
    }

    public Integer getFapRejectedFramesCount() {
        return fapRejectedFramesCount;
    }

    public void setFapRejectedFramesCount(Integer fapRejectedFramesCount) {
        this.fapRejectedFramesCount = fapRejectedFramesCount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public Long getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
