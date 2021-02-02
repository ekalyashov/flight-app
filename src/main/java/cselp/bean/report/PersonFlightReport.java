package cselp.bean.report;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PersonFlightReport implements Serializable {

    private Long personId;
    private List<PersonFlightRow> rows = new ArrayList<>();

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public List<PersonFlightRow> getRows() {
        return rows;
    }

    public void setRows(List<PersonFlightRow> rows) {
        this.rows = rows;
    }
}
