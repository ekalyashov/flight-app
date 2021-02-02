package cselp.bean;


import cselp.domain.local.Person;
import cselp.dto.User;

public class SessionData implements ISessionData {
    //user authorized by LDAP
    private LdapUser ldapUser;
    //person registered in SmartFlight DB
    private Person person;
    //cached dto object
    private User user;

    @Override
    public LdapUser getLdapUser() {
        return ldapUser;
    }

    @Override
    public void setLdapUser(LdapUser ldapUser) {
        this.ldapUser = ldapUser;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
