package cselp.domain.local;


public class TopStat {
    private Long count;
    private String origin;
    private String destination;
    private String role;
    private String tailNum;
    private Long personId;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTailNum() {
        return tailNum;
    }

    public void setTailNum(String tailNum) {
        this.tailNum = tailNum;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}
