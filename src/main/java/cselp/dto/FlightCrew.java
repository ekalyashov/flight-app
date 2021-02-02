package cselp.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FlightCrew implements Serializable {
    // task information
    private String task_num;
    private Timestamp task_datetime;
    // type of the crew, optional
    private String type;
    // crew members
    private List<FlightCrewMember> members = new ArrayList<>();

    public FlightCrew() {
    }

    public FlightCrew(String task_num, Timestamp task_datetime, String type) {
        this.task_num = task_num;
        this.task_datetime = task_datetime;
        this.type = type;
    }

    public String getTask_num() {
        return task_num;
    }

    public void setTask_num(String task_num) {
        this.task_num = task_num;
    }

    public Timestamp getTask_datetime() {
        return task_datetime;
    }

    public void setTask_datetime(Timestamp task_datetime) {
        this.task_datetime = task_datetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FlightCrewMember> getMembers() {
        return members;
    }

    public void setMembers(List<FlightCrewMember> members) {
        this.members = members;
    }
}
