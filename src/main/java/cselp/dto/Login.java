package cselp.dto;

/**
 * Bean contains data for logging on.
 */
public class Login {
    //personnel number as login
    private String tab_num;
    //password
    private String password;
    //is log on process strict (debug purposes only)
    private Boolean strict;

    public String getTab_num() {
        return tab_num;
    }

    public void setTab_num(String tab_num) {
        this.tab_num = tab_num;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }
}
