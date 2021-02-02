package cselp.domain.local;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Crew implements Serializable {
    private Long id;
    private Long legId;
    private Long squadronId;
    private String taskNum;
    private Timestamp taskDate;
    private String type;
    private int memberCount;
    private List<CrewMember> members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLegId() {
        return legId;
    }

    public void setLegId(Long legId) {
        this.legId = legId;
    }

    public Long getSquadronId() {
        return squadronId;
    }

    public void setSquadronId(Long squadronId) {
        this.squadronId = squadronId;
    }

    public String getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    public Timestamp getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Timestamp taskDate) {
        this.taskDate = taskDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public List<CrewMember> getMembers() {
        return members;
    }

    public void setMembers(List<CrewMember> members) {
        this.members = members;
    }
}
