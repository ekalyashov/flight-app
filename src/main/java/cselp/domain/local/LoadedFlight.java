package cselp.domain.local;


import java.io.Serializable;
import java.sql.Timestamp;

public class LoadedFlight implements Serializable {

    private Long id;
    private String fileName;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status;
    private String message;

    public LoadedFlight() {
    }

    public LoadedFlight(String fileName, Timestamp startTime, Timestamp endTime, String status, String message) {
        this.fileName = fileName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
