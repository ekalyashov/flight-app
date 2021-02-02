package cselp.bean;


import cselp.domain.local.Person;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

public class PersonContainerEvent extends ApplicationEvent {
    /**
     * Create a new PersonContainerEvent.
     *
     * @param source list of person ids for processing
     */
    public PersonContainerEvent(List<Person> source) {
        super(new ArrayList<>(source));
    }

    @SuppressWarnings("unchecked")
    public List<Person> getPersons() {
        return (List<Person>)getSource();
    }
}
