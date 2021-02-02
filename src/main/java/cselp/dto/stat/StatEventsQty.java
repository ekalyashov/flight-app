package cselp.dto.stat;

import java.io.Serializable;

/**
 * Quantity of events by severity
 * for any time diapason or any other key, just a container
 */
public class StatEventsQty implements Serializable{

    private int high;
    private int med;
    private int low;

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getMed() {
        return med;
    }

    public void setMed(int med) {
        this.med = med;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }
}
