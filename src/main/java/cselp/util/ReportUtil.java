package cselp.util;


import cselp.Const;
import cselp.bean.Dictionaries;
import cselp.domain.external.Aircraft;
import cselp.domain.external.Phase;
import cselp.domain.local.*;
import cselp.domain.report.PersonLeg;
import cselp.dto.*;
import cselp.dto.stat.*;
import cselp.exception.RAException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeComparator;

import java.sql.Timestamp;
import java.util.*;

/**
 * Utility class, contains methods to calculate some statistics.
 */
public class ReportUtil {
    private static final Log log = LogFactory.getLog(ReportUtil.class);

    /**
     * Utility method, composes list of legs to list of flight dto objects, using supplied reference maps.
     * @param legs list of Leg objects
     * @param tripMap map of trips
     * @param scoresMap map of event scores
     * @param persons map of persons
     * @param dict container for common reference maps
     * @param crewType crew type
     * @return list of flights
     */
    public static List<Flight> toFlights(Collection<Leg> legs, Map<Long, Trip> tripMap,
                                         Map<Long, List<EventScore>> scoresMap,
                                         Map <Long, Person> persons, Dictionaries dict, String crewType) {
        List<Flight> res = new ArrayList<>();
        for (Leg l : legs) {
            Flight f = toFlight(l, tripMap, scoresMap, persons, dict, crewType);
            res.add(f);
        }
        return res;
    }

    /**
     * Composes flight dto from supplied leg and reference maps.
     * @param leg Leg object
     * @param tripMap map of trips
     * @param scoresMap map of event scores
     * @param persons map of persons
     * @param dict container for common reference maps
     * @param crewType crew type
     * @return Flight object
     */
    public static Flight toFlight(Leg leg, Map<Long, Trip> tripMap,
                                         Map<Long, List<EventScore>> scoresMap,
                                         Map <Long, Person> persons, Dictionaries dict, String crewType) {
        Flight f = createNewFlight(leg.getId(), leg.getDepartureFact(), leg.getArrivalFact(),
                leg.getOrigin(), leg.getDestination(), leg.getTailNum(), dict.getAptCodesMap());
        Trip t = tripMap.get(leg.getTripId());
        if (t != null) {
            f.setFlight(t.getCarrier() + " " + t.getFlightNum());
        }
        Aircraft a = dict.getTailAircraftMap().get(leg.getTailNum());
        if (a != null) {
            f.setPlane(a.getConfiguration().getType().getType());
        }
        for (Crew c : leg.getCrews()) {
            if (crewType.equals(c.getType())) {
                FlightCrew fc = toFlightCrew(c, persons, dict.getSquadronMap());
                f.setCrew(fc);
                break;
            }
        }
        //events, statistic
        if (leg.getFlightId() != null) {
            List<EventScore> scores = scoresMap.get(leg.getFlightId());
            if (scores != null) {
                StatEventsQty stat = new StatEventsQty();
                int low = 0, med = 0, high = 0;
                int scoreSum = 0;
                for (EventScore es : scores) {
                    FlightEvent fe = new FlightEvent();
                    fe.setSeverity(es.getSeverityId());
                    fe.setType(es.getEventTypeName());
                    fe.setScore(es.getScore());
                    switch (es.getSeverityId()) {
                        case Const.EventSeverity.LOW:
                            low++;
                            break;
                        case Const.EventSeverity.MEDIUM:
                            med++;
                            break;
                        case Const.EventSeverity.HIGH:
                            high++;
                            break;
                        default:
                    }
                    if (es.getScore() != null) {
                        scoreSum += es.getScore();
                    }
                    Phase phase = dict.getPhaseMap().get(es.getPhaseId());
                    if (phase != null) {
                        fe.setPhase(phase.getName());
                    }
                    else {
                        fe.setPhase(es.getPhaseCode());
                    }
                    if (es.getEventTime() != null && es.getFlightDate() != null) {
                        fe.setTime(es.getEventTime().getTime() - es.getFlightDate().getTime());
                    }
                    f.getEvents().add(fe);
                }
                stat.setLow(low);
                stat.setMed(med);
                stat.setHigh(high);
                f.setStat_events(stat);
                f.setStat_score(scoreSum);
            }
        }
        return f;
    }

    private static FlightCrew toFlightCrew(Crew c, Map<Long, Person> persons, Map<Long, Squadron> squadronMap) {
        FlightCrew fc = new FlightCrew(c.getTaskNum(), c.getTaskDate(), c.getType());
        for (CrewMember cm : c.getMembers()) {
            FlightCrewMember fcm = toFlightCrewMember(cm, persons, squadronMap);
            fc.getMembers().add(fcm);
        }
        return fc;
    }

    private static FlightCrewMember toFlightCrewMember(CrewMember cm,
                                                       Map<Long, Person> persons, Map<Long, Squadron> squadronMap) {
        FlightCrewMember fcm = new FlightCrewMember();
        fcm.setFlight_role(cm.getFlightRole());
        Person p = persons.get(cm.getPersonId());
        if (p != null) {
            fcm.setTab_num(p.getTabNum());
            fcm.setFirst(p.getFirstName());
            fcm.setLast(p.getLastName());
            Squadron s = squadronMap.get(p.getSquadronId());
            if (s != null) {
                fcm.setSquadron(s.getName());
                if (s.getParent() != null) {
                    fcm.setDivision(s.getParent().getName());
                }
            }
        }
        return fcm;
    }

    private static PilotMinimums toPilotMinimums(PersonMinimum min) {
        PilotMinimums res = new PilotMinimums();
        if (min != null) {
            res.setLand_vis_h(min.getLandingVisHorizontal());
            res.setLand_vis_v(min.getLandingVisVertical());
            res.setTake_off_vis_h(min.getTakeOffVisHorizontal());
        }
        return res;
    }

    /**
     * Utility method to create User dto object using person data and supplied reference maps.
     * @param person Person object
     * @param squadronMap map squadronId -> Squadron
     * @param divisionRolesMap map divisionRoleId -> DivisionRole
     * @return new User object
     */
    public static User toUser(Person person, Map<Long, Squadron> squadronMap,
                              Map<Long, DivisionRole> divisionRolesMap) {
        User res = new User();
        res.setFirst(person.getFirstName());
        res.setLast(person.getLastName());
        res.setFull(person.getFullName());
        res.setTab_num(person.getTabNum());
        res.setId(person.getId());
        DivisionRole role = divisionRolesMap.get(person.getDivisionRoleId());
        if (role != null) {
            res.setDivision_role(role.getRole());
        }
        else {
            //todo remove when fill DIVISION_ROLE table
            res.setDivision_role(person.getDivisionRole());
        }
        if (person.getAppRoleId() != null) {
            res.setRole(String.valueOf(person.getAppRoleId()));
        }
        else {
            res.setRole(String.valueOf(Const.AppRoles.PILOT));
        }
        res.setSquadron_id(person.getSquadronId());
        Squadron s = squadronMap.get(person.getSquadronId());
        if (s != null) {
            res.setSquadron(s.getName());
            if (s.getParent() != null) {
                res.setDivision(s.getParent().getName());
            }
        }
        res.setMins(toPilotMinimums(person.getPersonMinimum()));
        return res;
    }

    /**
     * Composes statistics of flights performed by a pilot
     * @param personLegs list of legs performed by a pilot
     * @param squadronPersonsSum list of PersonsSumByMonth objects
     * @param airportCodesMap airportCodes -> AirportCode map
     * @return StatPilotFlights statistics
     */
    public static StatPilotFlights calculateFlightStat(List<Leg> personLegs,
                                                       List<PersonsSumByMonth> squadronPersonsSum,
                                                       Map<String, AirportCode> airportCodesMap) {
        StatPilotFlights spFlights = new StatPilotFlights();
        spFlights.setTotal(personLegs.size());
        Calendar cld = Calendar.getInstance();
        resetCalendar(cld);
        cld.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = cld.getTime();
        cld.set(Calendar.DAY_OF_YEAR, 1);
        Date yearStart = cld.getTime();
        cld = Calendar.getInstance();
        resetCalendar(cld);
        cld.add(Calendar.YEAR, -1);
        Date yearDeltaStart = cld.getTime();
        Date[] perMonthDate = getPerMonthDates();
        int[] history = new int[perMonthDate.length - 1];
        int monthCount = 0, yearCount = 0;
        List<Leg> yearDeltaLegs = new ArrayList<>();
        for (Leg l : personLegs) {
            if (l.getDeparturePlan().after(monthStart)) {
                monthCount++;
            }
            if (l.getDeparturePlan().after(yearStart)) {
                yearCount++;
            }
            if (l.getDeparturePlan().after(yearDeltaStart)) {
                yearDeltaLegs.add(l);
            }
            for (int i = 0; i < perMonthDate.length - 1; i++) {
                if (l.getDeparturePlan().compareTo(perMonthDate[i]) >= 0 &&
                        l.getDeparturePlan().compareTo(perMonthDate[i + 1]) < 0) {
                    history[i] = history[i] + 1;
                    break;
                }
            }
        }
        spFlights.setMonth(monthCount);
        spFlights.setYear(yearCount);
        spFlights.setHistory(history);

        cld = Calendar.getInstance();
        resetCalendar(cld);
        int[] average = new int[perMonthDate.length - 1];
        for (PersonsSumByMonth sbm : squadronPersonsSum) {
            //sql (SumByMonth) month january = 1, java january = 0
            cld.set(Calendar.MONTH, sbm.getMonth() - 1);
            cld.set(Calendar.YEAR, sbm.getYear());
            Date d = cld.getTime();
            for (int i = 0; i < perMonthDate.length - 1; i++) {
                if (d.compareTo(perMonthDate[i]) >= 0 &&
                        d.compareTo(perMonthDate[i + 1]) < 0) {
                    //average[i] = (int)(sbm.getSum() / squadronPersons.size());
                    average[i] = (int)(sbm.getSum() / sbm.getPersons());
                    break;
                }
            }
        }
        spFlights.setHistory_sqd_avg(average);
        DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
        cld = Calendar.getInstance();
        resetCalendar(cld);
        cld.set(Calendar.HOUR_OF_DAY, 6);
        Date date6hours = cld.getTime();
        cld.set(Calendar.HOUR_OF_DAY, 12);
        Date date12hours = cld.getTime();
        cld.set(Calendar.HOUR_OF_DAY, 18);
        Date date18hours = cld.getTime();
        int[] departures = new int[4];
        Calendar offset = Calendar.getInstance();
        for (Leg l : yearDeltaLegs) {
            Date d = l.getDeparturePlan();
            //set airport timezone
            d = setOriginTimezone(airportCodesMap, offset, l, d);
            if (comparator.compare(d, date6hours) < 0) {
                departures[0]++;
            }
            else if (comparator.compare(d, date12hours) < 0) {
                departures[1]++;
            }
            else if (comparator.compare(d, date18hours) < 0) {
                departures[2]++;
            }
            else {
                departures[3]++;
            }
        }
        spFlights.setDepartures(departures);
        List<Integer> per15minDates = new ArrayList<>();
        for (int min = 15; min <= 360; min = min + 15) {
            //to millis
            per15minDates.add(min * 60 * 1000);
        }
        int[] durations = new int[per15minDates.size() + 1];
        for (Leg l : yearDeltaLegs) {
            if (l.getArrivalFact() != null && l.getDepartureFact() != null) {
                int duration = (int)(l.getArrivalFact().getTime() - l.getDepartureFact().getTime());
                boolean found = false;
                for (int i = 0; i < per15minDates.size(); i++) {
                    Integer d = per15minDates.get(i);
                    if (duration < d) {
                        durations[i]++;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    durations[durations.length - 1]++;
                }
            }
        }
        spFlights.setDurations(durations);
        return spFlights;
    }

    private static Date setOriginTimezone(Map<String, AirportCode> airportCodesMap, Calendar offset, Leg l, Date d) {
        AirportCode depAC = DataUtil.getAirportCode(l.getOrigin());
        if (depAC != null) {
            depAC = airportCodesMap.get(depAC.getCodes());
            if (depAC != null && depAC.getAptZone() != null) {
                offset.setTime(d);
                offset.add(Calendar.MINUTE, depAC.getAptZone().intValue());
                d = offset.getTime();
            }
        }
        return d;
    }

    /**
     * Calculates statistics using supplied EventScore objects.
     * @param events list of EventScore objects
     * @param phaseMap phaseId -> Phase reference map
     * @return StatPilotEvents object
     */
    public static StatPilotEvents calculateEventStat(List<EventScore> events, Map<Short, Phase> phaseMap) {
        StatPilotEvents res = new StatPilotEvents();
        Calendar cld = Calendar.getInstance();
        resetCalendar(cld);
        cld.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = cld.getTime();
        cld.set(Calendar.DAY_OF_YEAR, 1);
        Date yearStart = cld.getTime();
        // sort events by time
        Date[] perMonthDate = getPerMonthDates();
        List<EventScore>[] perMonth = new List[perMonthDate.length - 1];
        List<EventScore> yearDeltaList = new ArrayList<>();
        List<EventScore> monthList = new ArrayList<>();
        List<EventScore> yearList = new ArrayList<>();
        for (int i = 0; i < perMonth.length; i++) {
            perMonth[i] = new ArrayList<>();
        }
        cld = Calendar.getInstance();
        resetCalendar(cld);
        cld.add(Calendar.YEAR, -1);
        Date yearDeltaStart = cld.getTime();
        for (EventScore es : events) {
            //todo use eventTime or flightDate ?
            if (es.getEventTime().after(yearDeltaStart)) {
                yearDeltaList.add(es);
            }
            if (es.getEventTime().after(monthStart)) {
                monthList.add(es);
            }
            if (es.getEventTime().after(yearStart)) {
                yearList.add(es);
            }
            for (int i = 0; i < perMonthDate.length - 1; i++) {
                if (es.getEventTime().compareTo(perMonthDate[i]) >= 0 &&
                        es.getEventTime().compareTo(perMonthDate[i + 1]) < 0) {
                    perMonth[i].add(es);
                    break;
                }
            }
        }
        //total events
        calculateEvents(events, res.getTotal());
        //year events
        calculateEvents(yearList, res.getYear());
        //month events
        calculateEvents(monthList, res.getMonth());
        //events for 12 months
        for (List<EventScore> mEvents : perMonth) {
            StatEventsQty qty = new StatEventsQty();
            calculateEvents(mEvents, qty);
            res.getHistory().add(qty);
        }
        Map<Short, List<EventScore>> phaseEventsMap = new HashMap<>();
        for (EventScore es : yearDeltaList) {
            ConvertUtil.addToMapEntry(phaseEventsMap, es.getPhaseId(), es);
        }
        for (Map.Entry<Short, List<EventScore>> e : phaseEventsMap.entrySet()) {
            StatPhaseEventsQty qty = new StatPhaseEventsQty();
            Phase phase = phaseMap.get(e.getKey());
            if (phase != null) {
                qty.setPhase(phase.getName());
            }
            calculateEvents(e.getValue(), qty);
            res.getBy_phase().add(qty);
        }
        return res;
    }

    private static Date[] getPerMonthDates() {
        Date[] perMonthDate = new Date[13];
        Calendar cld = Calendar.getInstance();
        resetCalendar(cld);
        cld.set(Calendar.DAY_OF_MONTH, 1);
        perMonthDate[perMonthDate.length - 1] = cld.getTime();
        for (int i = 2; i <= perMonthDate.length; i++) {
            cld.add(Calendar.MONTH, -1);
            perMonthDate[perMonthDate.length - i] = cld.getTime();
        }
        return perMonthDate;
    }

    /**
     * Utility method, clears all calendar fields.
     * @param calendar Calendar object
     * @return Calendar object
     */
    public static Calendar resetCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private static void calculateEvents(List<EventScore> events, StatEventsQty qty) {
        for (EventScore es : events) {
            switch (es.getSeverityId()) {
                case Const.EventSeverity.LOW:
                    qty.setLow(qty.getLow() + 1);
                    break;
                case Const.EventSeverity.MEDIUM:
                    qty.setMed(qty.getMed() + 1);
                    break;
                case Const.EventSeverity.HIGH:
                    qty.setHigh(qty.getHigh() + 1);
                    break;
                default:
            }
        }
    }

    /**
     * Calculates statistics of top records for a pilot
     * @param routes list of routes statistics
     * @param crews list of crews statistics
     * @param persons map personId -> Person
     * @param planes list of aircraft statistics
     * @param tailAircraftMap map registrationNumber -> Aircraft
     * @return StatPilotTops stat container
     */
    public static StatPilotTops calculatePilotTopStat(List<TopStat> routes, List<TopStat> crews, Map<Long, Person> persons,
                                                      List<TopStat> planes, Map<String, Aircraft> tailAircraftMap) {
        StatPilotTops tops = new StatPilotTops();
        for (TopStat ts : routes) {
            StatPilotTopRoute route = new StatPilotTopRoute(DataUtil.getStringAirportCode(ts.getOrigin()),
                    DataUtil.getStringAirportCode(ts.getDestination()), ts.getCount().intValue());
            tops.getRoutes().add(route);
        }
        for (TopStat ts : crews) {
            Person p = persons.get(ts.getPersonId());
            if (p != null) {
                String name = p.getLastName() + " " + p.getFirstName();
                StatPilotTopCrew crewCoMember = new StatPilotTopCrew(name, ts.getCount().intValue());
                tops.getCrew().add(crewCoMember);
            }
        }
        Map<String, Integer> topPlanes = new HashMap<>();
        for (TopStat ts : planes) {
            Aircraft a = tailAircraftMap.get(ts.getTailNum());
            if (a != null) {
                String type =  a.getConfiguration().getType().getType();
                Integer count = topPlanes.get(type);
                if (count == null) {
                    topPlanes.put(type, ts.getCount().intValue());
                }
                else {
                    topPlanes.put(type, count + ts.getCount().intValue());
                }
            }
        }
        for (Map.Entry<String, Integer> e : topPlanes.entrySet()) {
            StatPilotTopPlane topPlane = new StatPilotTopPlane(e.getKey(), e.getValue());
            tops.getPlanes().add(topPlane);
        }
        //sort StatPilotTopPlane - flights desc
        Collections.sort(tops.getPlanes(), new Comparator<StatPilotTopPlane>() {
            @Override
            public int compare(StatPilotTopPlane o1, StatPilotTopPlane o2) {
                return o2.getFlights().compareTo(o1.getFlights());
            }
        });
        return tops;
    }

    /**
     * Utility method, composes list of flight dto objects from list of PersonLeg objects,
     * using supplied reference maps.
     * @param legs list of PersonLeg objects
     * @param scoresMap map of event scores
     * @param persons map of persons
     * @param dict container for common reference maps
     * @param crewType crew type
     * @return list of flights
     */
    public static List<Flight> toFlights(Collection<PersonLeg> legs,
                                         Map<Long, List<EventScore>> scoresMap,
                                         Map<Long, Person> persons, Dictionaries dict,
                                         String crewType) {
        List<Flight> res = new ArrayList<>();
        for (PersonLeg l : legs) {
            Flight f = toFlight(l, scoresMap, persons, dict, crewType);
            res.add(f);
        }
        return res;
    }

    private static Flight toFlight(PersonLeg leg,
                                  Map<Long, List<EventScore>> scoresMap,
                                  Map<Long, Person> persons, Dictionaries dict,
                                  String crewType) {
        List<FlightEvent> flightEvents = new ArrayList<>();
        StatEventsQty stat = null;
        int scoreSum = 0;
        if (leg.getFlightId() != null) {
            stat = new StatEventsQty();
            //test - use fast search legs method, scores do not filled
            int low = 0, med = 0, high = 0;
            List<EventScore> scores = scoresMap.get(leg.getFlightId());
            if (scores != null) {
                for (EventScore es : scores) {
                    FlightEvent fe = new FlightEvent();
                    fe.setSeverity(es.getSeverityId());
                    fe.setType(es.getEventTypeName());
                    fe.setScore(es.getScore());
                    switch (es.getSeverityId()) {
                        case Const.EventSeverity.LOW:
                            low++;
                            break;
                        case Const.EventSeverity.MEDIUM:
                            med++;
                            break;
                        case Const.EventSeverity.HIGH:
                            high++;
                            break;
                        default:
                    }
                    if (es.getScore() != null) {
                        scoreSum += es.getScore();
                    }
                    Phase phase = dict.getPhaseMap().get(es.getPhaseId());
                    if (phase != null) {
                        fe.setPhase(phase.getName());
                    } else {
                        fe.setPhase(es.getPhaseCode());
                    }
                    if (es.getEventTime() != null && es.getFlightDate() != null) {
                        fe.setTime(es.getEventTime().getTime() - es.getFlightDate().getTime());
                    }
                    flightEvents.add(fe);
                }
            }
            stat.setLow(low);
            stat.setMed(med);
            stat.setHigh(high);
        }
        //create flight
        Flight f = createNewFlight(leg.getLegId(), leg.getDeparture(), leg.getArrival(),
                leg.getOrigin(), leg.getDestination(), leg.getTailNo(), dict.getAptCodesMap());
        f.setFlight(leg.getCarrier() + " " + leg.getFlightNo());
        Aircraft a = dict.getTailAircraftMap().get(leg.getTailNo());
        if (a != null) {
            f.setPlane(a.getConfiguration().getType().getType());
        }
        for (Crew c : leg.getCrews()) {
            if (crewType.equals(c.getType())) {
                FlightCrew fc = toFlightCrew(c, persons, dict.getSquadronMap());
                f.setCrew(fc);
                break;
            }
        }
        f.setStat_events(stat);
        f.setStat_score(scoreSum);
        f.getEvents().addAll(flightEvents);
        return f;
    }

    /**
     * Utility method, composes list of legs to list of flight dto objects, using supplied reference maps.
     * List of severity uses to filter interesting legs.
     * @param legs list of Leg objects
     * @param tripMap map of trips
     * @param scoresMap map of event scores
     * @param persons map of persons
     * @param dict container for common reference maps
     * @param severity list of EventSeverity values
     * @param crewType crew type
     * @return list of flights
     */
    public static List<Flight> toFlights(Collection<Leg> legs, Map<Long, Trip> tripMap,
                                         Map<Long, List<EventScore>> scoresMap,
                                         Map<Long, Person> persons,
                                         Dictionaries dict,
                                         Collection<Short> severity, String crewType) {
        boolean checkSeverity = severity != null;
        boolean sevLow = checkSeverity && severity.contains(Const.EventSeverity.LOW);
        boolean sevMed = checkSeverity && severity.contains(Const.EventSeverity.MEDIUM);
        boolean sevHigh = checkSeverity && severity.contains(Const.EventSeverity.HIGH);
        List<Flight> res = new ArrayList<>();
        for (Leg l : legs) {
            List<FlightEvent> flightEvents = new ArrayList<>();
            StatEventsQty stat = null;
            int scoreSum = 0;
            int low = 0, med = 0, high = 0;
            if (l.getFlightId() != null) {
                //check severity
                List<EventScore> scores = scoresMap.get(l.getFlightId());
                if (scores != null) {
                    for (EventScore es : scores) {
                        FlightEvent fe = new FlightEvent();
                        fe.setSeverity(es.getSeverityId());
                        fe.setType(es.getEventTypeName());
                        fe.setScore(es.getScore());
                        switch (es.getSeverityId()) {
                            case Const.EventSeverity.LOW:
                                low++;
                                break;
                            case Const.EventSeverity.MEDIUM:
                                med++;
                                break;
                            case Const.EventSeverity.HIGH:
                                high++;
                                break;
                            default:
                        }
                        if (es.getScore() != null) {
                            scoreSum += es.getScore();
                        }
                        Phase phase = dict.getPhaseMap().get(es.getPhaseId());
                        if (phase != null) {
                            fe.setPhase(phase.getName());
                        }
                        else {
                            fe.setPhase(es.getPhaseCode());
                        }
                        if (es.getEventTime() != null && es.getFlightDate() != null) {
                            fe.setTime(es.getEventTime().getTime() - es.getFlightDate().getTime());
                        }
                        flightEvents.add(fe);
                    }
                    stat = new StatEventsQty();
                    stat.setLow(low);
                    stat.setMed(med);
                    stat.setHigh(high);
                }
            }
            if (checkSeverity) {
                //new logic - if required severity events presents in flight - select this flight
                if (((low == 0) & sevLow) ||
                        ((med == 0) & sevMed) || ((high == 0) & sevHigh)) {
                    continue;
                }
            }
            //create flight
            Flight f = createNewFlight(l.getId(), l.getDepartureFact(), l.getArrivalFact(),
                    l.getOrigin(), l.getDestination(), l.getTailNum(), dict.getAptCodesMap());
            Trip t = tripMap.get(l.getTripId());
            if (t != null) {
                f.setFlight(t.getCarrier() + " " + t.getFlightNum());
            }
            Aircraft a = dict.getTailAircraftMap().get(l.getTailNum());
            if (a != null) {
                f.setPlane(a.getConfiguration().getType().getType());
            }
            for (Crew c : l.getCrews()) {
                if (crewType.equals(c.getType())) {
                    FlightCrew fc = toFlightCrew(c, persons, dict.getSquadronMap());
                    f.setCrew(fc);
                    break;
                }
            }
            f.setStat_events(stat);
            f.setStat_score(scoreSum);
            f.getEvents().addAll(flightEvents);
            res.add(f);
        }
        return res;
    }

    private static Flight createNewFlight(Long id, Timestamp depDate, Timestamp arrDate, String origin,
                                   String destination, String tail, Map<String, AirportCode> aptCodesMap) {
        //create apt codes from dest, origin records
        AirportCode depAirport = DataUtil.getAirportCode(origin);
        AirportCode arrAirport = DataUtil.getAirportCode(destination);
        //use real apt codes with valid time zone
        if (depAirport != null) {
            AirportCode ac = aptCodesMap.get(depAirport.getCodes());
            if (ac != null) {
                depAirport = ac;
            }
        }
        if (arrAirport != null) {
            AirportCode ac = aptCodesMap.get(arrAirport.getCodes());
            if (ac != null) {
                arrAirport = ac;
            }
        }
        Flight f = new Flight(id, depDate, arrDate,
                DataUtil.getStringAirportCode(depAirport), DataUtil.getStringAirportCode(arrAirport), tail);
        f.setDep_airport_tzo(DataUtil.getAirportZoneOffset(depAirport));
        f.setArr_airport_tzo(DataUtil.getAirportZoneOffset(arrAirport));
        return f;
    }
}
