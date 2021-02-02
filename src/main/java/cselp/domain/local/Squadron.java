package cselp.domain.local;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;


public class Squadron implements Serializable {

    private Long id;
    private Long divisionId;
    private String name;
    @JsonBackReference
    private Division parent;

    public Squadron() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Division getParent() {
        return parent;
    }

    public void setParent(Division parent) {
        this.parent = parent;
    }
}
