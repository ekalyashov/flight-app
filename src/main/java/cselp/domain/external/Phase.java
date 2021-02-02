package cselp.domain.external;


import java.io.Serializable;

public class Phase implements Serializable {
    private Short id;
    private String code;
    private String name;

    public Phase() {
    }

    public Phase(Short id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
