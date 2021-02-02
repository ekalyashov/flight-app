package cselp.service.report;


import cselp.Const;
import cselp.bean.Dictionaries;
import cselp.bean.FlightSearchCondition;
import cselp.bean.report.PersonFlightReport;
import cselp.bean.report.PersonFlightRow;
import cselp.domain.external.Aircraft;
import cselp.domain.external.Phase;
import cselp.domain.local.*;
import cselp.domain.report.PersonFlight;
import cselp.domain.report.PersonLeg;
import cselp.dto.Flight;
import cselp.dto.FlightSearchResult;
import cselp.exception.RAException;
import cselp.service.IFastStore;
import cselp.service.external.IFlightService;
import cselp.service.local.ILFlightService;
import cselp.service.local.IReportService;
import cselp.util.ReportUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cselp.dto.stat.StatPilot;
import cselp.dto.stat.StatPilotEvents;
import cselp.dto.stat.StatPilotFlights;
import cselp.dto.stat.StatPilotTops;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.*;

/**
 * Service class, implements methods to compose various reports.
 */
public class ReportComposer implements IReportComposer {
    private static final Log log = LogFactory.getLog(ReportComposer.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    public static final int NANOS_IN_MILLIS = 1000000;

    private Configuration appConfig;

    private ILFlightService lFlightService;
    private IFlightService flightService;
    private IReportService reportService;
    private IFastStore fastStore;

    private CacheManager cacheManager;

    private String crewPilotIdentifier = "";

    public void setAppProperties(Properties appProperties) {
        appConfig = ConfigurationConverter.getConfiguration(appProperties);
    }

    public void setlFlightService(ILFlightService lFlightService) {
        this.lFlightService = lFlightService;
    }

    public void setFlightService(IFlightService flightService) {
        this.flightService = flightService;
    }

    public void setReportService(IReportService reportService) {
        this.reportService = reportService;
    }

    public void setFastStore(IFastStore fastStore) {
        this.fastStore = fastStore;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Initialize service, set properties to application configuration values
     */
    public void init() {
        crewPilotIdentifier = appConfig.getString("crew.pilot.type", "");
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Returns PersonFlightReport for specified person as JSON string.
     * If report found in external cache, returns cached version, else creates new statistics and store into cache.
     * @param personId person identifier
     * @return PersonFlightReport for specified person as JSON string
     * @throws RAException if any
     * @throws JsonProcessingException if any
     */
    @Override
    public String getPersonFlightReport(Long personId) throws RAException, JsonProcessingException {
        String res = fastStore.findPersonFlightReport(personId);
        if (res == null) {
            if (fastStore.getLastError() != null) {
                log.warn("getPersonFlightReport - fastStore error : " + fastStore.getLastError().getMessage());
            }
            List<PersonFlight> flightsList = reportService.findPersonFlights(personId);
            Map<Long, Aircraft> aircraftMap = flightService.getAircraftMap();
            PersonFlightReport report = new PersonFlightReport();
            report.setPersonId(personId);
            for (PersonFlight pf : flightsList) {
                PersonFlightRow row = createPersonFlightRow(personId, pf, aircraftMap);
                report.getRows().add(row);
            }
            //ObjectMapper mapper = new ObjectMapper();
            res = mapper.writeValueAsString(report);
            fastStore.storePersonFlightReport(personId, res);
        }
        return res;
    }

    /**
     * Deletes PersonFlightReport from external cache.
     * @param personId person identifier
     */
    @Override
    public void dropPersonFlightReport(Long personId) {
        fastStore.dropPersonFlightReport(personId);
    }

    private PersonFlightRow createPersonFlightRow(Long personId,
                                                  PersonFlight pf, Map<Long, Aircraft> aircraftMap) {
        PersonFlightRow row = new PersonFlightRow();
        row.setPersonId(personId);
        row.setDate(pf.getDate());
        row.setTime(pf.getTime());
        row.setFlight(pf.getFlight());
        row.setOrigin(pf.getOrigin());
        row.setDest(pf.getDest());
        row.setTailNo(pf.getTailNo());
        row.setRole(pf.getRole());
        if (!pf.getScores().isEmpty()) {
            Long aircraftId = pf.getScores().get(0).getAircraftId();
            Aircraft a = aircraftMap.get(aircraftId);
            if (a != null) {
                row.setPlane(a.getConfiguration().getType().getType());
            }
        }
        if (pf.getScore() != null) {
            row.setScore(pf.getScore());
        }
        if (pf.getHiEvents() != null) {
            row.setHiEvents(pf.getHiEvents());
        }
        if (pf.getMedEvents() != null) {
            row.setMedEvents(pf.getMedEvents());
        }
        if (pf.getLowEvents() != null) {
            row.setLowEvents(pf.getLowEvents());
        }
        return row;
    }

    /**
     * Searches flights using specified conditions. DB view VW_FLIGHT_BY_PERSON used to select flights.
     * @param condition search conditions
     * @return FlightSearchResult dto object
     * @throws RAException if any
     */
    @Override
    public FlightSearchResult searchFlights(FlightSearchCondition condition) throws RAException {
        long start = System.nanoTime();
        Pair<Long, List<PersonLeg>> res = reportService.findPersonLegsOpt(condition);
        List<PersonLeg> legs = res.getRight();
        String timing = "searchFlights timing : findPersonLegs[" + legs.size() + "] - " +
                (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        Set<Long> personIds = new HashSet<>();
        List<Long> flightIds = new ArrayList<>();
        for (PersonLeg pl : legs) {
            if (pl.getFlightId() != null) {
                flightIds.add(pl.getFlightId());
            }
            for (Crew c : pl.getCrews()) {
                for (CrewMember cm : c.getMembers()) {
                    personIds.add(cm.getPersonId());
                }
            }
        }
        Dictionaries dict = new Dictionaries();
        dict.setTailAircraftMap(flightService.getTailAircraftMap());
        timing += ", load tailAircraftMap[" + dict.getTailAircraftMap().size() + "] - " +
                (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        dict.setPhaseMap(flightService.getPhaseMap());
        timing += ", load phaseMap[" + dict.getPhaseMap().size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        dict.setSquadronMap(lFlightService.findSquadronMap());
        timing += ", load squadronMap[" + dict.getSquadronMap().size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        Map<Long, List<EventScore>> scoresMap = reportService.findEventScoresMap(flightIds);
        timing += ", load scores [" + scoresMap.size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        Map<Long, Person> persons = getPersonMap(personIds);
        timing += ", load persons[" + persons.size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        dict.setAptCodesMap(lFlightService.getAirportCodesMap());
        List<Flight> list = ReportUtil.toFlights(legs, scoresMap,
                persons, dict, crewPilotIdentifier);
        log.info(timing);
        FlightSearchResult result = new FlightSearchResult(list.size(), list);
        result.setFirst(condition.getFirstResult() == null ? 0 : condition.getFirstResult());
        result.setTotal(res.getLeft());
        return result;
    }

    /**
     * Searches flights using specified conditions. Part of conditions used to select trips,
     * then legs filtered by severity conditions. Result can be inconsistent if amount defined in conditions.
     * @param condition search conditions
     * @return FlightSearchResult dto object
     * @throws RAException if any
     */
    @Override
    public FlightSearchResult findFlights(FlightSearchCondition condition) throws RAException {
        long start = System.nanoTime();
        Pair<Long, List<Trip>> res = reportService.findTrips(condition);
        List<Trip>  trips = res.getValue();
        String timing = "findFlights timing : findTrips[" + trips.size() + "] - " +
                (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        Set<Long> personIds = new HashSet<>();
        List<Long> flightIds = new ArrayList<>();
        List<Leg> legs = new ArrayList<>();
        Map<Long, Trip> tripMap = new HashMap<>();
        for (Trip t : trips) {
            tripMap.put(t.getId(), t);
            legs.addAll(t.getLegs());
            for (Leg l : t.getLegs()) {
                if (l.getFlightId() != null) {
                    flightIds.add(l.getFlightId());
                }
                for (Crew c : l.getCrews()) {
                    for (CrewMember cm : c.getMembers()) {
                        personIds.add(cm.getPersonId());
                    }
                }
            }
        }
        Dictionaries dict = new Dictionaries();
        dict.setTailAircraftMap(flightService.getTailAircraftMap());
        dict.setAptCodesMap(lFlightService.getAirportCodesMap());
        timing += ", load tailAircraftMap[" + dict.getTailAircraftMap().size() + "] - " +
                (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        dict.setPhaseMap(flightService.getPhaseMap());
        timing += ", load phaseMap[" + dict.getPhaseMap().size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        dict.setSquadronMap(lFlightService.findSquadronMap());
        timing += ", load squadronMap[" + dict.getSquadronMap().size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        Map<Long, List<EventScore>> scoresMap = reportService.findEventScoresMap(flightIds);
        timing += ", load scores [" + scoresMap.size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        start = System.nanoTime();
        Map<Long, Person> persons = getPersonMap(personIds);
        timing += ", load persons[" + persons.size() + "] - " + (System.nanoTime() - start)/ NANOS_IN_MILLIS;
        dict.setAptCodesMap(lFlightService.getAirportCodesMap());
        List<Flight> list = ReportUtil.toFlights(legs, tripMap, scoresMap,
                persons, dict, condition.getSeverity(), crewPilotIdentifier);
        log.info(timing);
        FlightSearchResult result = new FlightSearchResult(list.size(), list);
        result.setFirst(condition.getFirstResult() == null ? 0 : condition.getFirstResult());
        result.setTotal(res.getLeft());
        return result;
    }

    /**
     * Returns flights withing specified date range.
     * @param from start date for flight search
     * @param to stop date for flight search
     * @return list of flights
     * @throws RAException if any
     */
    @Override
    public List<Flight> getFlights(Date from, Date to) throws RAException {
        List<Trip> trips = reportService.findTrips(from, to);
        Set<Long> personIds = new HashSet<>();
        List<Long> flightIds = new ArrayList<>();
        List<Leg> legs = new ArrayList<>();
        Map<Long, Trip> tripMap = new HashMap<>();
        for (Trip t : trips) {
            tripMap.put(t.getId(), t);
            legs.addAll(t.getLegs());
            for (Leg l : t.getLegs()) {
                if (l.getFlightId() != null) {
                    flightIds.add(l.getFlightId());
                }
                for (Crew c : l.getCrews()) {
                    for (CrewMember cm : c.getMembers()) {
                        personIds.add(cm.getPersonId());
                    }
                }
            }
        }
        Map<Long, Person> persons = getPersonMap(personIds);
        Map<Long, List<EventScore>> scoresMap = reportService.findEventScoresMap(flightIds);
        Dictionaries dict = new Dictionaries();
        dict.setTailAircraftMap(flightService.getTailAircraftMap());
        dict.setSquadronMap(lFlightService.findSquadronMap());
        dict.setPhaseMap(flightService.getPhaseMap());
        dict.setAptCodesMap(lFlightService.getAirportCodesMap());
        List<Flight> res = ReportUtil.toFlights(legs, tripMap, scoresMap, persons,
                dict, crewPilotIdentifier);
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flight getFlight(Long legId) throws RAException {
        Leg leg = reportService.getLeg(legId);
        Set<Long> personIds = new HashSet<>();
        for (Crew c : leg.getCrews()) {
            for (CrewMember cm : c.getMembers()) {
                personIds.add(cm.getPersonId());
            }
        }
        Map<Long, Trip> tripMap = reportService.getTripMap(Arrays.asList(leg.getTripId()));
        Map<Long, Person> persons = getPersonMap(personIds);
        Map<Long, List<EventScore>> scoresMap = reportService.findEventScoresMap(Arrays.asList(leg.getFlightId()));
        Dictionaries dict = new Dictionaries();
        dict.setTailAircraftMap(flightService.getTailAircraftMap());
        dict.setPhaseMap(flightService.getPhaseMap());
        dict.setSquadronMap(lFlightService.findSquadronMap());
        dict.setAptCodesMap(lFlightService.getAirportCodesMap());
        Flight res = ReportUtil.toFlight(leg, tripMap, scoresMap, persons, dict, crewPilotIdentifier);
        return res;
    }

    /**
     * Returns last flights for specified person. 'Count' value limits returned list size.
     * @param personId person identifier
     * @param count required number of flights
     * @return list of flights
     * @throws RAException if any
     */
    @Override
    public List<Flight> getLastFlights(Long personId, int count) throws RAException {
        List<Leg> legs = reportService.findLastLegs(personId, count);
        Set<Long> personIds = new HashSet<>();
        List<Long> flightIds = new ArrayList<>();
        Set<Long> tripIds = new HashSet<>();
        for (Leg l : legs) {
            tripIds.add(l.getTripId());
            if (l.getFlightId() != null) {
                flightIds.add(l.getFlightId());
            }
            for (Crew c : l.getCrews()) {
                for (CrewMember cm : c.getMembers()) {
                    personIds.add(cm.getPersonId());
                }
            }
        }
        Map<Long, Trip> tripMap = reportService.getTripMap(tripIds);
        Map<Long, Person> persons = getPersonMap(personIds);
        Dictionaries dict = new Dictionaries();
        dict.setTailAircraftMap(flightService.getTailAircraftMap());
        dict.setSquadronMap(lFlightService.findSquadronMap());
        dict.setPhaseMap(flightService.getPhaseMap());
        dict.setAptCodesMap(lFlightService.getAirportCodesMap());
        Map<Long, List<EventScore>> scoresMap = reportService.findEventScoresMap(flightIds);
        List<Flight> res = ReportUtil.toFlights(legs, tripMap, scoresMap, persons,
                dict, crewPilotIdentifier);
        return res;
    }

    /**
     * Returns full person statistics - serialized JSON string of StatPilot object.
     * If such statistics found in external cache, returns cached version, else creates new statistics and store into cache.
     * @param p Person object
     * @return statistics as JSON string
     * @throws RAException if any
     * @throws JsonProcessingException if any
     */
    @Override
    public String getFullPersonStatistics(Person p) throws RAException, JsonProcessingException {
        String res = fastStore.findFullPersonStatistics(p.getId());
        if (res == null) {
            res = createFullPersonStatReport(p);
            fastStore.storeFullPersonStatistics(p.getId(), res);
        }
        return res;
    }

    /**
     * Updates full person statistics in external cache for subsequent usage.
     * @param p Person object
     * @throws RAException if any
     * @throws JsonProcessingException if any
     */
    @Override
    public void updateFullPersonStatistics(Person p) throws RAException, JsonProcessingException {
        String res = createFullPersonStatReport(p);
        fastStore.storeFullPersonStatistics(p.getId(), res);
    }

    private String createFullPersonStatReport(Person p) throws RAException, JsonProcessingException {
        List<EventScore> events = reportService.findPersonEventScores(p.getId());
        StatPilot stat = new StatPilot();
        Map<Short, Phase> phaseMap = flightService.getPhaseMap();
        StatPilotEvents spEvents = ReportUtil.calculateEventStat(events, phaseMap);
        stat.setEvents(spEvents);
        //StatPilotFlights
        List<Leg> personLegs = reportService.findPersonLegs(p.getId());
        List<PersonsSumByMonth> squadronPersonsSum = lFlightService.findSquadronPersonSumLegs(p.getSquadronId());
        Map<String, AirportCode> airportCodesMap = lFlightService.getAirportCodesMap();
        StatPilotFlights spFlights = ReportUtil.calculateFlightStat(personLegs, squadronPersonsSum, airportCodesMap);
        spFlights.setFlights(getLastFlights(p.getId(), Const.LAST_FLIGHTS_COUNT));
        stat.setFlights(spFlights);
        //StatPilotTops
        Calendar cld = Calendar.getInstance();
        ReportUtil.resetCalendar(cld);
        cld.add(Calendar.YEAR, -1);
        Date yearDeltaStart = cld.getTime();
        List<TopStat> routes = lFlightService.findTopRoutesStat(p.getId(), yearDeltaStart, Const.TOP_STAT_COUNT);
        //List<TopStat> roles = lFlightService.findTopFlightRolesStat(p.getId(), Const.TOP_STAT_COUNT);
        //first row - own pilot flights, get TOP_STAT_COUNT +  rows and remove first
        List<TopStat> crews = lFlightService.findPilotTopCrewStat(p.getId(), yearDeltaStart, Const.TOP_STAT_COUNT + 1);
        if (!crews.isEmpty()) {
            crews.remove(0);
        }
        List<Long> personIds = new ArrayList<>();
        for (TopStat crew : crews) {
            personIds.add(crew.getPersonId());
        }
        Map<Long, Person> persons = getPersonMap(personIds);
        //planes tail to type conversion - all planes required, we have many tail_nums per plane type
        List<TopStat> planes = lFlightService.findTopPlanesStat(p.getId(), yearDeltaStart, null);
        Map<String, Aircraft> tailAircraftMap = flightService.getTailAircraftMap();
        StatPilotTops tops = ReportUtil.calculatePilotTopStat(routes, crews, persons, planes, tailAircraftMap);
        if (tops.getPlanes().size() > Const.TOP_STAT_COUNT) {
            tops.setPlanes(new ArrayList<>(tops.getPlanes().subList(0, Const.TOP_STAT_COUNT)));
        }
        stat.setTops(tops);
        return mapper.writeValueAsString(stat);
    }

    Map<Long, Person> getPersonMap(Collection<Long> personIds) throws RAException {
        Map<Long, Person> res = new HashMap<>();
        try {
            List<Long> unknownIds = new ArrayList<>();
            Cache cache = cacheManager.getCache(Const.Cache.PERSONS);
            if (cache == null) {
                throw new IllegalArgumentException("Unknown cache " + Const.Cache.PERSONS);
            }
            else {
                for (Long personId : personIds) {
                    Person p = cache.get(personId, Person.class);
                    if (p != null) {
                        res.put(p.getId(), p);
                    } else {
                        unknownIds.add(personId);

                    }
                }
                if (!unknownIds.isEmpty()) {
                    Map<Long, Person> newPersons = lFlightService.getPersonMap(unknownIds);
                    res.putAll(newPersons);
                    for (Map.Entry<Long, Person> e : newPersons.entrySet()) {
                        cache.put(e.getKey(), e.getValue());
                    }
                }
            }
        }
        catch (Exception ex) {
            log.error("getPersonMap error", ex);
            res = lFlightService.getPersonMap(personIds);
            Cache cache = cacheManager.getCache(Const.Cache.PERSONS);
            if (cache != null) {
                for (Map.Entry<Long, Person> e : res.entrySet()) {
                    cache.put(e.getKey(), e.getValue());
                }
            }
        }
        return res;
    }
}
