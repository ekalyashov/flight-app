package cselp.domain.external;


import java.io.Serializable;
import java.sql.Timestamp;

public class FapConfiguration implements Serializable {

    private Long id;
    private Long frameId;
    private String classId;
    private String name;
    private String description;
    private Integer version;
    private Integer revision;
    private Integer buildNumber;
    private Timestamp creationDate;
    private Timestamp buildDate;
    private Boolean deidEnable;
    private Boolean flightDateDeid;
    private Boolean acRegDeid;
    private Long parentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFrameId() {
        return frameId;
    }

    public void setFrameId(Long frameId) {
        this.frameId = frameId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(Timestamp buildDate) {
        this.buildDate = buildDate;
    }

    public Boolean getDeidEnable() {
        return deidEnable;
    }

    public void setDeidEnable(Boolean deidEnable) {
        this.deidEnable = deidEnable;
    }

    public Boolean getFlightDateDeid() {
        return flightDateDeid;
    }

    public void setFlightDateDeid(Boolean flightDateDeid) {
        this.flightDateDeid = flightDateDeid;
    }

    public Boolean getAcRegDeid() {
        return acRegDeid;
    }

    public void setAcRegDeid(Boolean acRegDeid) {
        this.acRegDeid = acRegDeid;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
