package cselp.zk;

import cselp.bean.LdapUser;
import cselp.bean.LegSearchFlags;
import cselp.domain.external.FlightWrapper;
import cselp.domain.local.Leg;
import cselp.zk.service.IAdminService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AdminViewModel {
    private static final Log log = LogFactory.getLog(AdminViewModel.class);

    @WireVariable
    private IAdminService adminService;

    @WireVariable
    private Properties appConfig;

    private String userName;
    private List<LdapUser> userList;
    private List<Leg> unlinkedLegs;
    private List<FlightWrapper> similarFlights;
    private Date from;
    private Date to;
    private Leg selectedLeg;
    private boolean searchTailNo = false;
    private boolean searchOrigin = false;
    private boolean searchDestination = false;
    private boolean searchDeparture = false;
    private boolean searchArrival = false;
    private int deltaTime;
    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<LdapUser> getUserList() {
        return userList;
    }

    public List<FlightWrapper> getSimilarFlights() {
        return similarFlights;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Leg getSelectedLeg() {
        return selectedLeg;
    }

    public void setSelectedLeg(Leg selectedLeg) {
        this.selectedLeg = selectedLeg;
    }

    public boolean isSearchTailNo() {
        return searchTailNo;
    }

    public void setSearchTailNo(boolean searchTailNo) {
        this.searchTailNo = searchTailNo;
    }

    public boolean isSearchOrigin() {
        return searchOrigin;
    }

    public void setSearchOrigin(boolean searchOrigin) {
        this.searchOrigin = searchOrigin;
    }

    public boolean isSearchDestination() {
        return searchDestination;
    }

    public void setSearchDestination(boolean searchDestination) {
        this.searchDestination = searchDestination;
    }

    public boolean isSearchDeparture() {
        return searchDeparture;
    }

    public void setSearchDeparture(boolean searchDeparture) {
        this.searchDeparture = searchDeparture;
    }

    public boolean isSearchArrival() {
        return searchArrival;
    }

    public void setSearchArrival(boolean searchArrival) {
        this.searchArrival = searchArrival;
    }

    public int getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(int deltaTime) {
        this.deltaTime = deltaTime;
    }

    public List<Leg> getUnlinkedLegs() {
        return unlinkedLegs;
    }

    @Init
    public void init() {
        try {
            String sDelta = appConfig.getProperty("flight.times.delta");
            deltaTime = Integer.parseInt(sDelta);
            log.info("flight sync delta time = " + deltaTime);
        } catch (Exception e) {
            log.error("Error parsing flight.times.delta ", e);
        }
    }

    @Command
    @NotifyChange("userList")
    public void getLdapUsers() {
        try {
            userList = adminService.findUsers(userName);
        }
        catch (Exception e) {
            log.error("Error in getLdapUsers", e);
        }
    }

    @Command
    @NotifyChange("unlinkedLegs")
    public void findUnlinkedLegs() {
        try {
            if (from != null && to != null) {
                unlinkedLegs = adminService.findUnlinkedLegs(from, to);
            }
            else {
                unlinkedLegs = new ArrayList<>();
            }
        }
        catch (Exception e) {
            log.error("Error in findUnlinkedLegs", e);
        }
    }

    @Command
    @NotifyChange({"similarFlights", "message"})
    public void findSimilarFlights() {
        try {
            if (selectedLeg != null) {
                if (!searchDeparture && !searchArrival) {
                    message = "Departure or Arrival required for search.";
                    similarFlights = new ArrayList<>();
                }
                else {
                    LegSearchFlags cond = new LegSearchFlags(selectedLeg.getId(),
                            searchTailNo, searchOrigin, searchDestination, searchDeparture, searchArrival);
                    Long delta = null;
                    if (deltaTime > 0) {
                        delta = Long.valueOf(deltaTime) * 1000;
                    }
                    similarFlights = adminService.findSimilarFlights(cond, delta);
                }
            }
            else {
                message = "No leg selected.";
                similarFlights = new ArrayList<>();
            }
        }
        catch (Exception e) {
            log.error("Error in findSimilarFlights", e);
            message = e.toString() + " - " + e.getLocalizedMessage();
            similarFlights = new ArrayList<>();
        }
    }
}
