package cselp.bean;


public class LdapUser {
    private String userName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String logonName;
    private String dn;

    public String getFullName() {
        String res = (firstName == null) ? "" : firstName + " ";
        res += (middleName == null) ? "" : middleName;
        res += (lastName == null) ? "" : " " +lastName;
        return res;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLogonName() {
        return logonName;
    }

    public void setLogonName(String logonName) {
        this.logonName = logonName;
    }
}
