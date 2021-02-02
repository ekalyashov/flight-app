package cselp.dto.stat;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Statistics of scores for a pilot
 */
public class StatPilotScores implements Serializable {
    // total score for the whole time
    private Integer total;
    // total score in this calendar year
    private Integer year;
    // total score in this calendar month
    private Integer month;
    // total scores for last 12 months, NOT including the current one,
    // order is [latest month, ... , newest month],
    private List<Integer> history = new ArrayList<>();
    // average score for a division member for last 12 months, NOT including the current one,
    // order is [latest month, ... , newest month],
    private List<Integer> history_div_avg = new ArrayList<>();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<Integer> getHistory() {
        return history;
    }

    public void setHistory(List<Integer> history) {
        this.history = history;
    }

    public List<Integer> getHistory_div_avg() {
        return history_div_avg;
    }

    public void setHistory_div_avg(List<Integer> history_div_avg) {
        this.history_div_avg = history_div_avg;
    }
}
