package cselp.domain.local;

import java.io.Serializable;

/**
 * Container for statistics:
 * sum of legs, executed at certain month and year.
 */
public class SumByMonth implements Serializable {
    //sum of legs
    private Long sum;
    private Integer month;
    private Integer year;

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
