package cselp.bean;


public class LegSearchFlags {

    private Long id;
    private boolean searchTailNo = false;
    private boolean searchOrigin = false;
    private boolean searchDestination = false;
    private boolean searchDeparture = false;
    private boolean searchArrival = false;

    public LegSearchFlags(Long id, boolean searchTailNo, boolean searchOrigin, boolean searchDestination,
                          boolean searchDeparture, boolean searchArrival) {
        this.id = id;
        this.searchTailNo = searchTailNo;
        this.searchOrigin = searchOrigin;
        this.searchDestination = searchDestination;
        this.searchDeparture = searchDeparture;
        this.searchArrival = searchArrival;
    }

    public Long getId() {
        return id;
    }

    public boolean isSearchTailNo() {
        return searchTailNo;
    }

    public boolean isSearchOrigin() {
        return searchOrigin;
    }

    public boolean isSearchDestination() {
        return searchDestination;
    }

    public boolean isSearchDeparture() {
        return searchDeparture;
    }

    public boolean isSearchArrival() {
        return searchArrival;
    }
}
