package cselp.dto.stat;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Statistics of top records for a pilot
 */
public class StatPilotTops implements Serializable {
    // 5-10 top routes, performed by a pilot, for any time diapason
    private List<StatPilotTopRoute> routes = new ArrayList<>();
    // 5-10 top planes, a pilot was flying
    private List<StatPilotTopPlane> planes = new ArrayList<>();
    // 5-10 top crew members
    private List<StatPilotTopCrew> crew = new ArrayList<>();

    public List<StatPilotTopRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<StatPilotTopRoute> routes) {
        this.routes = routes;
    }

    public List<StatPilotTopPlane> getPlanes() {
        return planes;
    }

    public void setPlanes(List<StatPilotTopPlane> planes) {
        this.planes = planes;
    }

    public List<StatPilotTopCrew> getCrew() {
        return crew;
    }

    public void setCrew(List<StatPilotTopCrew> crew) {
        this.crew = crew;
    }
}
