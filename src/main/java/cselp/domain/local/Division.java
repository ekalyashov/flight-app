package cselp.domain.local;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.List;


public class Division implements Serializable {

    private Long id;
    private String name;
    @JsonManagedReference
    private List<Squadron> squadrons;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Squadron> getSquadrons() {
        return squadrons;
    }

    public void setSquadrons(List<Squadron> squadrons) {
        this.squadrons = squadrons;
    }
}
