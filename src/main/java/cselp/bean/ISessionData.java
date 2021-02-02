package cselp.bean;


import cselp.domain.local.Person;
import cselp.dto.User;

import java.io.Serializable;

public interface ISessionData extends Serializable {
    LdapUser getLdapUser();

    void setLdapUser(LdapUser ldapUser);

    Person getPerson();

    void setPerson(Person person);

    User getUser();

    void setUser(User user);
}
