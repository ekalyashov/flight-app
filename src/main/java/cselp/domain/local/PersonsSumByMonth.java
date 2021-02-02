package cselp.domain.local;

/**
 * Container for person statistics:
 * count of persons and sum of legs executed by persons, per month.
 */
public class PersonsSumByMonth extends SumByMonth {
    //count of persons
    private Long persons;

    public Long getPersons() {
        return persons;
    }

    public void setPersons(Long persons) {
        this.persons = persons;
    }
}
