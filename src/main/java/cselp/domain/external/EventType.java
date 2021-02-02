package cselp.domain.external;

import java.io.Serializable;
import java.sql.Timestamp;


public class EventType implements Serializable {

    private Long id;
    private Long dtbId;
    private Long eventNum;
    private String name;
    private String description;
    private String eventClass;
    private Boolean alert;
    private Boolean standard;
    private Long parentId;
    private Timestamp updateDate;

    public EventType() {
    }

    public EventType(Long id, Long dtbId, Long eventNum, String name, String description) {
        this.id = id;
        this.dtbId = dtbId;
        this.eventNum = eventNum;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDtbId() {
        return dtbId;
    }

    public void setDtbId(Long dtbId) {
        this.dtbId = dtbId;
    }

    public Long getEventNum() {
        return eventNum;
    }

    public void setEventNum(Long eventNum) {
        this.eventNum = eventNum;
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

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public Boolean getStandard() {
        return standard;
    }

    public void setStandard(Boolean standard) {
        this.standard = standard;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }
}
