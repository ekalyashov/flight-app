package cselp.domain.local;


import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable {

    private Long id;
    private Long squadronId;
    private Long divisionRoleId;
    private Long appRoleId;
    private String tabNum;
    private String firstName;
    private String lastName;
    private String fullName;
    private String gender;
    private Date birthDate;
    private String divisionRole;
    private PersonMinimum personMinimum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSquadronId() {
        return squadronId;
    }

    public void setSquadronId(Long squadronId) {
        this.squadronId = squadronId;
    }

    public Long getDivisionRoleId() {
        return divisionRoleId;
    }

    public void setDivisionRoleId(Long divisionRoleId) {
        this.divisionRoleId = divisionRoleId;
    }

    public Long getAppRoleId() {
        return appRoleId;
    }

    public void setAppRoleId(Long appRoleId) {
        this.appRoleId = appRoleId;
    }

    public String getTabNum() {
        return tabNum;
    }

    public void setTabNum(String tabNum) {
        this.tabNum = tabNum;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getDivisionRole() {
        return divisionRole;
    }

    public void setDivisionRole(String divisionRole) {
        this.divisionRole = divisionRole;
    }

    public PersonMinimum getPersonMinimum() {
        return personMinimum;
    }

    public void setPersonMinimum(PersonMinimum personMinimum) {
        this.personMinimum = personMinimum;
    }
}
