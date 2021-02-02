package cselp.service;

import cselp.domain.external.EventPrimaryParameter;
import cselp.domain.external.EventType;
import cselp.domain.external.Flight;
import cselp.domain.external.Phase;
import cselp.domain.local.*;
import cselp.domain.opensky.*;
import cselp.exception.RAException;
import cselp.exception.RAUserException;
import cselp.service.external.IFlightService;
import cselp.service.local.ILFlightService;
import cselp.util.ConvertUtil;
import cselp.util.DataUtil;
import cselp.Const;
import cselp.bean.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.*;

/**
 * Implementation of synchronization service. Links AirFASE flight to OpenSky flight.
 */
public class DataSyncService implements IDataSyncService, ApplicationContextAware {
    private static final Log log = LogFactory.getLog(DataSyncService.class);
    //interval to calculate leg synchronization due date. dueDate = now - interval
    private int syncDueInterval = 2;
    private int syncDueTimeUnit = Calendar.MONTH;
    //subtract shift to arrival fact to use as touch down time if touch down is null (minutes)
    public static final int TOUCH_DOWN_FIX_SHIFT = 15;
    private static final long MS_IN_DAY = 24*60*60*1000;

    public static final int MAX_RECORDS_FOR_UPDATE = 500;

    //interval to calculate first log generation if leg synchronization failed,  in days
    private int logDueInterval1 = 3;
    private int logDueInterval2 = 14;  //two weeks
    private int touchDownFixShift = TOUCH_DOWN_FIX_SHIFT;

    private Configuration appConfig;
    private ApplicationContext appContext;
    private IFlightService flightService;
    private ILFlightService lFlightService;
    private IFastStore fastStore;

    private String crewPilotIdentifier = "";
    private List<String> ignoreProperties;
    private List<Class> nestedClasses;

    public void setAppProperties(Properties appProperties) {
        appConfig = ConfigurationConverter.getConfiguration(appProperties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public void setFlightService(IFlightService flightService) {
        this.flightService = flightService;
    }

    public void setlFlightService(ILFlightService lFlightService) {
        this.lFlightService = lFlightService;
    }

    public void setFastStore(IFastStore fastStore) {
        this.fastStore = fastStore;
    }

    /**
     * Initialize service, set properties to application configuration values
     */
    public void init() {
        crewPilotIdentifier = appConfig.getString("crew.pilot.type", "");
        syncDueInterval = appConfig.getInt("sync.due.interval", 2);
        syncDueTimeUnit = appConfig.getInt("sync.due.time.unit", Calendar.MONTH);
        logDueInterval1 = appConfig.getInt("log.due.interval.1", 3);
        logDueInterval2 = appConfig.getInt("log.due.interval.2", 14);
        touchDownFixShift = appConfig.getInt("leg.touch.down.fix.shift", TOUCH_DOWN_FIX_SHIFT);

        ignoreProperties = Arrays.asList("id", "personId");
        nestedClasses = new ArrayList<>();
        nestedClasses.add(Person.class);
        nestedClasses.add(PersonMinimum.class);
    }

    /**
     * Parse OpenSky document, extract and store flights.
     * @param xml OpenSky document
     * @param fileName processed file name, used in generated log messages
     * @return result status message
     * @throws RAException if any
     */
    @Override
    public String parseFlights(String xml, String fileName) throws RAException {
        Counters counters = new Counters();
        try {
            counters.start = new Date();
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<PERFORMEDFLIGHTSType> unmarshalledObject =
                    (JAXBElement<PERFORMEDFLIGHTSType>)unmarshaller.unmarshal(new StringReader(xml));
            PERFORMEDFLIGHTSType flightsRoot = unmarshalledObject.getValue();
            List<FLIGHTType> flights = flightsRoot.getFLIGHT();
            //remove crews that isn't pilots (f.e. stewards)
            removeUnusedCrews(flights);
            //prepare maps of all crew members, divisions, squadrons, contained in document
            Map<String, Set<String>> divisionMap = new HashMap<>();
            Map<Long, DivisionRole> divisionRolesMap = new HashMap<>();
            Map<String, MEMBERSType> membersMap = prepareMembers(flights, divisionMap, divisionRolesMap);
            //check and create divisions and squadrons
            Map<String, Map<String, Long>> divSqIds = checkDivisions(divisionMap);
            //check and create divisionRoles
            Map<Long, DivisionRole> oldDivisionRoles = lFlightService.getDivisionRolesMap();
            for (Map.Entry<Long, DivisionRole> e : divisionRolesMap.entrySet()) {
                DivisionRole oldRole = oldDivisionRoles.get(e.getKey());
                if (!isEquals(oldRole, e.getValue())) {
                    lFlightService.saveDivisionRole(e.getValue());
                }
            }
            //check and create persons
            fastStore.init();
            Map<String, Person> personMap = processMembers(counters, membersMap, divSqIds);
            List<Trip> trips = new ArrayList<>();
            for (FLIGHTType f : flights) {
                Timestamp actualDate = DataUtil.toTimestamp(f.getACTUALDATE());
                counters.firstFlightTs = getMin(counters.firstFlightTs, actualDate);
                counters.lastFlightTs = getMax(counters.lastFlightTs, actualDate);
                List<Trip> tripList = lFlightService.findTrips(f.getCARRIER(), f.getFLIGHTNO(),
                        DataUtil.toTimestamp(f.getFLIGHTDATE()), actualDate,
                        f.getTAILNO(), f.getORIGIN(), f.getDESTINATION(), f.getFLTKIND());
                if (tripList.isEmpty()) {
                    //create trip, else ignore
                    Trip t = DataUtil.newTrip(f, personMap, divSqIds);
                    //validate legs
                    List<Pair<Leg, ErrorMessage>> errors = new ArrayList<>();
                    for (Leg leg : t.getLegs()) {
                        ErrorMessage error = DataUtil.validateLeg(leg);
                        if (error != null) {
                            errors.add(new ImmutablePair<>(leg, error));
                        }
                    }
                    t = lFlightService.createTrip(t);
                    trips.add(t);
                    //now this legs with ids, after create trip
                    for (Pair<Leg, ErrorMessage> pair : errors) {
                        sendLegValidationError(fileName, pair);
                    }
                }
                else {
                    counters.duplicatedTrips++;
                    Trip t = tripList.get(0);
                    sendDupFlightError(fileName, f, t.getId());
                }
            }
            Map<String, Pair<Leg, AirportCode>> airportCodes = new HashMap<>();
            for (Trip t : trips) {
                Map<String, Pair<Leg, AirportCode>> codes = DataUtil.getAirportCodes(t.getLegs());
                airportCodes.putAll(codes);
                counters.legCount += t.getLegs().size();
            }
            //save new airport codes
            saveNewAirportCodes(airportCodes, fileName);
            //update airport zones
            updateAirportZones(airportCodes);
            String message = "Found flights : " + flights.size() +
                    ", members " + membersMap.size() + ", new trips " + trips.size() +
                    (counters.duplicatedTrips == 0 ? "" : (", duplicatedTrips " + counters.duplicatedTrips)) +
                    ", new legs " + counters.legCount +
                    ", new persons " + counters.createdPersonCount + ", updated persons " + counters.updatedPersonCount;
            log.info(message);
            sendResultMessage(fileName, trips, counters);
            return message;
        } catch (Exception e) {
            log.error("parseFlights error", e);
            sendParseError(fileName, e);
            throw new RAUserException("parseFlights error : " + e.getMessage(), e);
        }
    }

    private void updateAirportZones(Map<String, Pair<Leg, AirportCode>> airportCodes) throws RAException {
        Map<String, AirportCode> acMap = lFlightService.getAirportCodesMap();
        for (Map.Entry<String, Pair<Leg, AirportCode>> e : airportCodes.entrySet()) {
            AirportCode oldAC = acMap.get(e.getValue().getRight().getCodes());
            if (oldAC != null && !ObjectUtils.equals(oldAC.getAptZone(), e.getValue().getRight().getAptZone())) {
                oldAC.setAptZone(e.getValue().getRight().getAptZone());
                lFlightService.updateAirportCode(oldAC);
            }
        }
    }

    private boolean isEquals(DivisionRole r1, DivisionRole r2) {
        if (r1 == r2) {
            return true;
        }
        if (r1 != null) {
            if (r2 == null) {
                return false;
            }
            else {
                return ObjectUtils.equals(r1.getId(), r2.getId()) &&
                        ObjectUtils.equals(r1.getRole(), r2.getRole()) &&
                        ObjectUtils.equals(r1.getDescription(), r2.getDescription());
            }
        }
        else return false;
    }

    /*private static class DivisionRoleComparator implements Comparator<DivisionRole> {

        @Override
        public int compare(DivisionRole o1, DivisionRole o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 != null) {
                if (o2 == null) {
                    return 1;
                }
                else {
                    if (ObjectUtils.compare(o1.getId(), o2.getId())
                }
            }
            else return -1;
    } */

    private Map<String, Person> processMembers(Counters counters, Map<String, MEMBERSType> membersMap,
                                               Map<String, Map<String, Long>> divSqIds)
            throws RAException {
        Map<String, Person> personMap = new HashMap<>();
        for (Map.Entry<String, MEMBERSType> e : membersMap.entrySet()) {
            MEMBERSType mt = e.getValue();
            Person p = lFlightService.findPerson(e.getKey());
            if (p == null) {
                //create person
                p = DataUtil.newPerson(mt, divSqIds);
                p = lFlightService.createPerson(p);
                counters.createdPersonCount++;
            }
            else {
                Person source = DataUtil.newPerson(mt, divSqIds);
                Map<String, String> diffProps =
                        DataUtil.getChangedProperties(p, source, ignoreProperties, nestedClasses);
                if (DataUtil.compareAndUpdate(p, source)) {
                    p = lFlightService.updatePerson(p);
                    counters.updatedPersonCount++;
                    log.info("Person " + p.getTabNum() + " updated");
                    JSONObject json = new JSONObject(diffProps);
                    fastStore.addPersonUpdate(p.getTabNum(), json);
                }
            }
            if (p != null) {
                personMap.put(p.getTabNum(), p);
            }
        }
        return personMap;
    }

    private void sendResultMessage(String fileName, List<Trip> trips, Counters counters) {
        Map<String, String> properties = new HashMap<>();
        properties.put("file", fileName);
        properties.put("start", DataUtil.dfs.get().format(counters.start));
        properties.put("end", DataUtil.dfs.get().format(new Date()));
        properties.put("success", "true");
        put(properties, "flights", trips.size());
        put(properties, "dup_flights", counters.duplicatedTrips);
        if (counters.firstFlightTs != null) {
            properties.put("flight_first", DataUtil.df.get().format(counters.firstFlightTs));
        }
        if (counters.lastFlightTs != null) {
            properties.put("flight_last", DataUtil.df.get().format(counters.lastFlightTs));
        }
        put(properties, "legs", counters.legCount);
        put(properties, "new_legs", counters.legCount);
        put(properties, "new_members", counters.createdPersonCount);
        put(properties, "updated_members", counters.updatedPersonCount);
        appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.LOAD_RESULT, Level.INFO, properties)));
    }

    private void sendParseError(String fileName, Exception e) {
        Map<String, String> properties = new HashMap<>();
        properties.put("file", fileName);
        properties.put("success", "false");
        appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.LOAD_RESULT, Level.INFO, e, properties)));
    }

    /**
     * Extracts from all flights crew members, divisions, squadrons, fills members and divisions map
     * @param flights OpenSky flights, extracted from input xml
     * @param divisionMap divisions map
     * @return map of MEMBERSType
     */
    private Map<String, MEMBERSType> prepareMembers(List<FLIGHTType> flights,
                                                    Map<String, Set<String>> divisionMap,
                                                    Map<Long, DivisionRole> divisionRolesMap) {
        Map<String, MEMBERSType> membersMap = new HashMap<>();
        for (FLIGHTType f : flights) {
            for (LEGType leg : f.getLEG()) {
                for (CREWTASKType crewTask : leg.getCREWTASK()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Crew " + crewTask.getNO() + ", type " + crewTask.getTYPE());
                    }
                    if (crewTask.getDIVISION() != null) {
                        String crewDivisionName = StringUtils.trim(crewTask.getDIVISION().getNAME());
                        String crewSquadronName = StringUtils.trim(crewTask.getDIVISION().getNO());
                        ConvertUtil.addToMapEntry(divisionMap, crewDivisionName, crewSquadronName, HashSet.class);
                    }
                    List<MEMBERSType> mList = crewTask.getMEMBERS();
                    if (mList == null) {
                        continue;
                    }
                    for (MEMBERSType mt : mList) {
                        if (mt.getMEMBER() != null) {
                            String personNum = mt.getMEMBER().getNO();
                            membersMap.put(personNum, mt);
                            if (mt.getMEMBER().getDIVISION() != null) {
                                String divisionName = StringUtils.trim(mt.getMEMBER().getDIVISION().getNAME());
                                String squadronName = StringUtils.trim(mt.getMEMBER().getDIVISION().getNO());
                                ConvertUtil.addToMapEntry(divisionMap, divisionName, squadronName, HashSet.class);
                            }
                        }
                        ROLEType rt = mt.getROLE();
                        DivisionRole role = new DivisionRole();
                        role.setId(rt.getDIVISIONROLEID());
                        role.setRole(rt.getDIVISIONROLE());
                        divisionRolesMap.put(role.getId(), role);
                    }
                }
            }
        }
        return membersMap;
    }

    private void removeUnusedCrews(List<FLIGHTType> flights) {
        for (FLIGHTType f : flights) {
            for (LEGType leg : f.getLEG()) {
                Iterator<CREWTASKType> it = leg.getCREWTASK().iterator();
                while (it.hasNext()) {
                    CREWTASKType crewTask = it.next();
                    if (!crewPilotIdentifier.equals(crewTask.getTYPE())) {
                        it.remove();
                    }
                }
            }
        }
    }

    private void sendLegValidationError(String fileName, Pair<Leg, ErrorMessage> pair) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("file", fileName);
        properties.put("flight", String.valueOf(pair.getLeft().getTripId()));
        properties.put("leg", String.valueOf(pair.getLeft().getId()));
        JSONArray elements = new JSONArray(pair.getRight().getMessages());
        properties.put("elements", elements);
        appContext.publishEvent(new LogContainerEvent(
                DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.LEG_INCOMPLETE,
                        Level.ERROR, properties)));
    }

    private void sendDupFlightError(String fileName, FLIGHTType f, Long flightId) {
        String message = "Duplicate flight ignored: " + f.getCARRIER() + f.getFLIGHTNO() +
                "; tailNo " + f.getTAILNO()  + "; Flight date " + f.getFLIGHTDATE() +
                "; Origin " + f.getORIGIN() + "; Destination " + f.getDESTINATION();
        log.warn(message);
        Map<String, String> properties = new HashMap<>();
        properties.put("file", fileName);
        put(properties, "flight", flightId);
        properties.put("flight_no", f.getFLIGHTNO());
        properties.put("carrier", f.getCARRIER());
        properties.put("flight_date", f.getFLIGHTDATE());
        appContext.publishEvent(new LogContainerEvent(
                DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.FLIGHT_DUP,
                        Level.WARN, properties)));
    }

    private Timestamp getMax(Timestamp oldTs, Timestamp newTs) {
        if (oldTs == null || oldTs.before(newTs)) {
            return newTs;
        }
        return oldTs;
    }

    private Timestamp getMin(Timestamp oldTs, Timestamp newTs) {
        if (oldTs == null || oldTs.after(newTs)) {
            return newTs;
        }
        return oldTs;
    }

    private void put(Map<String, String> properties, String key, Number value) {
        properties.put(key, String.valueOf(value));
    }

    private void saveNewAirportCodes(Map<String, Pair<Leg, AirportCode>> airportCodes, String fileName)
            throws RAException {
        for (Pair<Leg, AirportCode> p : airportCodes.values()) {
            try {
                Map<String, String> properties = new HashMap<>();
                properties.put("file", fileName);
                put(properties, "flight", p.getLeft().getTripId());
                put(properties, "leg", p.getLeft().getId());
                AirportCode ac = p.getRight();
                if (ac.getIcaoCode() != null && ac.getIataCode() != null) {
                    properties.put("iata", ac.getIataCode());
                    properties.put("icao", ac.getIcaoCode());
                    createNewAirportCode(ac, Const.LogCode.AIRPORT_DUP_ICAO_IATA,
                            Const.LogCode.AIRPORT_CONFLICT_ICAO_IATA, properties);
                } else if (ac.getIcaoCode() != null) {
                    properties.put("icao", ac.getIcaoCode());
                    createNewAirportCode(ac, Const.LogCode.AIRPORT_DUP_ICAO, Const.LogCode.AIRPORT_CONFLICT_ICAO, properties);
                } else if (ac.getIataCode() != null) {
                    properties.put("iata", ac.getIataCode());
                    createNewAirportCode(ac, Const.LogCode.AIRPORT_DUP_IATA, Const.LogCode.AIRPORT_CONFLICT_IATA, properties);
                } else {
                    properties.put("code", ac.getCodes());
                    appContext.publishEvent(new LogContainerEvent(
                            DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.AIRPORT_NOCODES,
                                    Level.ERROR, properties)));
                }
            }
            catch (Exception e) {
                appContext.publishEvent(new LogContainerEvent(
                        DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.UNKNOWN_ERROR, Level.ERROR,e)));
            }
        }
    }

    private void createNewAirportCode(AirportCode newCode, String nonUniqueErrorCode,
                                      String nonEqualErrorCode, Map<String, String> properties)
            throws RAException {
        List<AirportCode> codes = lFlightService.findAirportCodeByAllCodes(newCode);
        if (codes.isEmpty()) {
            //new code
            lFlightService.createAirportCode(newCode);
        }
        else if (codes.size() > 1) {
            appContext.publishEvent(new LogContainerEvent(
                    DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LOAD, nonUniqueErrorCode,
                            Level.WARN, properties)));
        }
        else {
            if (!newCode.isCodesEquals(codes.get(0))) {
                properties.put("crt", newCode.getCrtCode());
                properties.put("ikao", newCode.getIcaoCyrillicCode());
                appContext.publishEvent(new LogContainerEvent(
                        DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LOAD, nonEqualErrorCode,
                                Level.WARN, properties)));
            }
        }
    }

    private Map<String, Map<String, Long>> checkDivisions(Map<String, Set<String>> divisionMap) throws RAException {
        Map<String, Map<String,Long>> divSqIds = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : divisionMap.entrySet()) {
            String divName = e.getKey();
            Division div = lFlightService.getDivision(divName);
            if (div == null) {
                div = lFlightService.createDivision(divName);
            }
            Map<String,Long> sqIds = divSqIds.get(divName);
            if (sqIds == null) {
                sqIds = new HashMap<>();
                divSqIds.put(divName, sqIds);
            }
            Set<String> sqNames = e.getValue();
            if (sqNames != null) {
                for (String sqName : sqNames) {
                    Squadron sq = lFlightService.getSquadron(div.getId(), sqName);
                    if (sq == null) {
                        sq = lFlightService.createSquadron(div.getId(), sqName);
                    }
                    sqIds.put(sqName, sq.getId());
                }
            }
        }
        return divSqIds;
    }

    private void checkAndAddLog(long interval, Leg leg, String errorCode, Level level)
            throws RAException {
        long now = System.currentTimeMillis();
        if ((now - leg.getArrivalPlan().getTime()) >= interval) {
            leg.setLastLogDate(new Timestamp(now));
            lFlightService.updateLeg(leg);
            Map<String, String> properties = new HashMap<>();
            put(properties, "flight", leg.getTripId());
            put(properties, "leg", leg.getId());
            appContext.publishEvent(new LogContainerEvent(
                    DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LINK, errorCode, level, now, properties)));
        }
    }

    /**
     * Synchronize OpenSky and AirFASE flights.
     * OpenSky flights without links to AirFASE flights selects from database according specified from and to dates.
     * @param from from date
     * @param to to date
     * @throws RAException if any
     */
    @Override
    public void syncFlightsAndLegs(Date from, Date to) throws RAException {
        Date start = new Date();
        List<Leg> legs = lFlightService.findUnlinkedLegs(from, to);
        List<Long>  synchronizedLegs = new ArrayList<>();
        Map<String, List<String>> regNumMap = lFlightService.getAircraftRegNumMap();
        Map<Pair<Long, Short>, EventTypeScore> etsMap = lFlightService.getEventTypeScoresMap();
        AirportCodesContainer acc = flightService.getAirportCodes();
        Map<Long, EventType> eventTypeMap = flightService.getEventTypesMap();
        Map<Short, Phase> phaseMap = flightService.getPhaseMap();
        for (Leg leg : legs) {
            try {
                Pair<ErrorMessage, Leg> result = DataUtil.validateAndFixLeg(leg, touchDownFixShift);
                if (result.getLeft() != null) {
                    log.error(result.getLeft().getMessage() + ", " + result.getLeft().getMessages());
                }
                else {
                    LegSearchCondition cond = new LegSearchCondition(result.getRight());
                    fillAirportCodes(cond, acc);
                    if (CollectionUtils.isEmpty(cond.getDestinationIds()) ||
                            CollectionUtils.isEmpty(cond.getOriginIds())) {
                        sendAirportMissingError(cond);
                    }
                    else {
                        List<Flight> flights = flightService.findSuitableFlight(cond, regNumMap);
                        if (flights.size() == 0) {
                            String message = "No suitable flights selected for leg " + leg.getId() +
                                    " trip " + leg.getTripId();
                            log.warn(message);
                        } else {
                            Long flightId = flights.get(0).getId();
                            if (flights.size() > 1) {
                                sendMultiFlightsMatchError(leg, flights, flightId);
                            }
                            leg.setFlightId(flightId);
                            List<EventPrimaryParameter> evParameters =
                                    flightService.findEventPrimaryParametersByFlight(flightId);
                            lFlightService.updateLegAndScores(leg, evParameters, etsMap, eventTypeMap, phaseMap);
                            synchronizedLegs.add(leg.getId());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error in syncFlightsAndLegs.", e);
                appContext.publishEvent(new LogContainerEvent(
                        DataUtil.createLogMessage(Const.LogClassifier.GENERAL, Const.LogCode.UNKNOWN_ERROR,
                                Level.ERROR, e)));
            }
        }
        List<Person> syncPersons = lFlightService.findPersonsForLegs(synchronizedLegs);
        appContext.publishEvent(new PersonContainerEvent(syncPersons));
        log.info("syncFlightsAndLegs finished");
        Date end = new Date();
        Map<String, String> properties = new HashMap<>();
        put(properties, "linked", synchronizedLegs.size());
        put(properties, "unlinked", legs.size() - synchronizedLegs.size());
        String sStart = DataUtil.dfs.get().format(start);
        String sEnd = DataUtil.dfs.get().format(end);
        String sFrom = DataUtil.dfs.get().format(from);
        String sTo = DataUtil.dfs.get().format(to);
        properties.put("start", sStart);
        properties.put("end", sEnd);
        properties.put("fromDate", sFrom);
        properties.put("toDate", sTo);
        log.info("syncFlightsAndLegs results: linked " + synchronizedLegs +
                ", unlinked " + (legs.size() - synchronizedLegs.size()) + ", start " + sStart + ", end " + sEnd +
        ", fromDate " + sFrom + ", toDate " + sTo);
        appContext.publishEvent(new LogContainerEvent(
                DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LINK, Const.LogCode.LINK_RESULT, Level.INFO, properties)));

    }

    private void sendAirportMissingError(LegSearchCondition cond) {
        Map<String, String> properties = new HashMap<>();
        put(properties, "flight", cond.getTripId());
        put(properties, "leg", cond.getId());
        String code = null;
        if (CollectionUtils.isEmpty(cond.getDestinationIds())) {
            code = cond.getDestination();
        }
        if (CollectionUtils.isEmpty(cond.getOriginIds())) {
            code = cond.getOrigin();
        }
        if (code != null) {
            properties.put("code", code);
        }
        appContext.publishEvent(new LogContainerEvent(
                DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LINK, Const.LogCode.AIRPORT_MISSING,
                        Level.WARN, properties)));
    }

    /**
     * Synchronize OpenSky and AirFASE flights.
     * Synchronization applied only to OpenSky flights, not linked to AirFASE flights,
     * and not older than due period (2 month)
     * @throws RAException if any
     */
    @Override
    public void syncFlightsAndLegs() throws RAException {
        Date start = new Date();
        Calendar cld = Calendar.getInstance();
        cld.add(syncDueTimeUnit, -syncDueInterval);
        Timestamp dueDate = new Timestamp(cld.getTimeInMillis());
        List<Leg> legs = lFlightService.findUnlinkedLegs(dueDate);
        List<Long>  synchronizedLegs = new ArrayList<>();
        Map<String, List<String>> regNumMap = lFlightService.getAircraftRegNumMap();
        Map<Pair<Long, Short>, EventTypeScore> etsMap = lFlightService.getEventTypeScoresMap();
        AirportCodesContainer acc = flightService.getAirportCodes();
        Map<Long, EventType> eventTypeMap = flightService.getEventTypesMap();
        Map<Short, Phase> phaseMap = flightService.getPhaseMap();
        for (Leg leg : legs) {
            try {
                Pair<ErrorMessage, Leg> result = DataUtil.validateAndFixLeg(leg, touchDownFixShift);
                if (result.getLeft() != null) {
                    log.error(result.getLeft().getMessage() + ", " + result.getLeft().getMessages());
                }
                else {
                    LegSearchCondition cond = new LegSearchCondition(result.getRight());
                    fillAirportCodes(cond, acc);
                    if (CollectionUtils.isEmpty(cond.getDestinationIds()) ||
                            CollectionUtils.isEmpty(cond.getOriginIds())) {
                        sendAirportMissingError(cond);
                    }
                    else {
                        List<Flight> flights = flightService.findSuitableFlight(cond, regNumMap);
                        if (flights.size() == 0) {
                            logUnlinkedLeg(cld, leg);
                        } else {
                            Long flightId = flights.get(0).getId();
                            if (flights.size() > 1) {
                                sendMultiFlightsMatchError(leg, flights, flightId);
                            }
                            leg.setFlightId(flightId);
                            List<EventPrimaryParameter> evParameters =
                                    flightService.findEventPrimaryParametersByFlight(flightId);
                            lFlightService.updateLegAndScores(leg, evParameters, etsMap, eventTypeMap, phaseMap);
                            synchronizedLegs.add(leg.getId());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error in syncFlightsAndLegs.", e);
                appContext.publishEvent(new LogContainerEvent(
                        DataUtil.createLogMessage(Const.LogClassifier.GENERAL, Const.LogCode.UNKNOWN_ERROR,
                                Level.ERROR, e)));
            }
        }
        List<Person> syncPersons = lFlightService.findPersonsForLegs(synchronizedLegs);
        appContext.publishEvent(new PersonContainerEvent(syncPersons));
        log.info("syncFlightsAndLegs finished");
        Date end = new Date();
        Map<String, String> properties = new HashMap<>();
        put(properties, "linked", synchronizedLegs.size());
        put(properties, "unlinked", legs.size() - synchronizedLegs.size());
        long oldUnlinkedLegsCount = lFlightService.countOldUnlinkedLegs(dueDate);
        put(properties, "unlinked_old", oldUnlinkedLegsCount);
        properties.put("start", DataUtil.dfs.get().format(start));
        properties.put("end", DataUtil.dfs.get().format(end));
        appContext.publishEvent(new LogContainerEvent(
                DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LINK, Const.LogCode.LINK_RESULT, Level.INFO, properties)));
    }

    private void logUnlinkedLeg(Calendar cld, Leg leg) throws RAException {
        String message = "No suitable flights selected for leg " + leg.getId() +
                " trip " + leg.getTripId();
        log.warn(message);
        //calculate last log dates and emit log messages
        // logDueInterval3 minus 2 days to take into account due date
        // (legs below this date filtered, error log could not be generated)
        long logDueInterval3 = System.currentTimeMillis() - cld.getTimeInMillis() - MS_IN_DAY * 2;
        if (leg.getLastLogDate() == null) {
            //no log yet, generate first INFO log
            checkAndAddLog(logDueInterval1 * MS_IN_DAY, leg, Const.LogCode.LINK_TIMEOUT1, Level.WARN);
        } else if ((leg.getLastLogDate().getTime() - leg.getArrivalPlan().getTime()) <
                (logDueInterval2 * MS_IN_DAY)) {
            //first level log found, generate WARN log
            checkAndAddLog(logDueInterval2 * MS_IN_DAY, leg, Const.LogCode.LINK_TIMEOUT2, Level.WARN);
        } else if ((leg.getLastLogDate().getTime() - leg.getArrivalPlan().getTime()) < logDueInterval3) {
            //second level log found, generate ERROR log
            checkAndAddLog(logDueInterval3, leg, Const.LogCode.LINK_TIMEOUT3, Level.ERROR);
        }
    }

    private static void fillAirportCodes(LegSearchCondition cond, AirportCodesContainer acc) {
        Long id = acc.getCodesToAptMap().get(
                StringUtils.defaultString(cond.getOriginIcao()) + cond.getOriginIata());
        if (id != null) {
            cond.getOriginIds().add(id);
        }
        id = acc.getIcaoToAptMap().get(cond.getOriginIcao());
        if (id != null) {
            cond.getOriginIds().add(id);
        }
        List<Long> ids = acc.getIataToAptMap().get(cond.getOriginIata());
        if (ids != null) {
            cond.getOriginIds().addAll(ids);
        }
        id = acc.getCodesToAptMap().get(
                StringUtils.defaultString(cond.getDestinationIcao()) + cond.getDestinationIata());
        if (id != null) {
            cond.getDestinationIds().add(id);
        }
        id = acc.getIcaoToAptMap().get(cond.getDestinationIcao());
        if (id != null) {
            cond.getDestinationIds().add(id);
        }
        ids = acc.getIataToAptMap().get(cond.getDestinationIata());
        if (ids != null) {
            cond.getDestinationIds().addAll(ids);
        }
    }

    private void sendMultiFlightsMatchError(Leg leg, List<Flight> flights, Long flightId) {
        String message = "Too many suitable flights: " + flights.size() +
                ", first selected id = " + flights.get(0).getId();
        log.warn(message);
        Map<String, String> properties = new HashMap<>();
        put(properties, "flight", leg.getTripId());
        put(properties, "leg", leg.getId());
        put(properties, "found", flights.size());
        put(properties, "linked", flightId);
        List<Long> flightIds = new ArrayList<>();
        for (Flight f : flights) {
            flightIds.add(f.getId());
        }
        properties.put("flightIds", flightIds.toString());
        appContext.publishEvent(new LogContainerEvent(
                DataUtil.createLogMessage(Const.LogClassifier.OS_FLIGHT_LINK, Const.LogCode.FLIGHT_MMATCH,
                        Level.WARN, properties)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateEventScores(Integer maxResults) throws RAException {
        Map<Long, EventType> eventTypeMap = flightService.getEventTypesMap();
        Map<Short, Phase> phaseMap = flightService.getPhaseMap();
        //select scores where flightDate, phaseCode or eventTypeName is null
        int updatedRecords = 0;
        if (maxResults != null) {
            if (maxResults > 0) {
                //update first 'maxResults' records
                UpdateResult res = updateIncompleteEventScores(maxResults, 0, eventTypeMap, phaseMap);
                updatedRecords = res.updatedRecords;
            } else {
                //update all incomplete records
                long firstId = 0;
                UpdateResult res;
                do {
                    res = updateIncompleteEventScores(MAX_RECORDS_FOR_UPDATE, firstId, eventTypeMap, phaseMap);
                    firstId = res.lastId + 1;
                    updatedRecords += res.updatedRecords;
                } while (res.updatedRecords > 0);
            }
        }
        return updatedRecords;
    }

    private static class UpdateResult {
        long lastId;
        int updatedRecords;

        public UpdateResult(long lastId, int updatedRecords) {
            this.lastId = lastId;
            this.updatedRecords = updatedRecords;
        }
    }

    private UpdateResult updateIncompleteEventScores(int maxResults, long firstId,
                                             Map<Long, EventType> eventTypeMap,
                                             Map<Short, Phase> phaseMap) throws RAException {
        List<EventScore> eventScores = lFlightService.findIncompleteEventScores(maxResults, firstId);
        if (eventScores.isEmpty()) {
            return new UpdateResult(0, 0);
        }
        //load external data
        Set<Long> flightIds = new HashSet<>();
        for (EventScore es : eventScores) {
            if (es.getFlightId() != null) {
                flightIds.add(es.getFlightId());
            }
        }
        List<Flight> flights = flightService.getFlights(flightIds);
        Map<Long, Flight> flightsMap = new HashMap<>();
        for (Flight f : flights) {
            flightsMap.put(f.getId(), f);
        }
        //update EventScore
        for (EventScore es : eventScores) {
            Phase phase = phaseMap.get(es.getPhaseId());
            if (phase != null) {
                es.setPhaseCode(phase.getCode());
            }
            EventType eventType = eventTypeMap.get(es.getEventTypeId());
            if (eventType != null) {
                es.setEventTypeName(eventType.getName());
            }
            Flight flight = flightsMap.get(es.getFlightId());
            if (flight != null) {
                es.setFlightDate(flight.getStartDate());
            }
            lFlightService.updateEventScore(es);
        }
        return new UpdateResult(eventScores.get(eventScores.size() - 1).getEventId(), eventScores.size());
    }

    private static class Counters {
        public int updatedPersonCount = 0;
        public int createdPersonCount = 0;
        public int duplicatedTrips = 0;
        public int legCount = 0;
        public Date start = null;
        public Timestamp firstFlightTs = null;
        public Timestamp lastFlightTs = null;
    }

}
