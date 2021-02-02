package cselp.service.local;


import cselp.Const;
import cselp.bean.LogContainerEvent;
import cselp.bean.PersonSearchCondition;
import cselp.dao.local.ILFlightDao;
import cselp.domain.external.EventPrimaryParameter;
import cselp.domain.external.EventType;
import cselp.domain.external.Phase;
import cselp.domain.local.*;
import cselp.exception.RAException;
import cselp.util.DataUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.sql.Timestamp;
import java.util.*;

public class LFlightService implements ILFlightService, ApplicationContextAware {
    private static final Log log = LogFactory.getLog(LFlightService.class);

    private ILFlightDao dao;
    private ApplicationContext appContext;

    public void setDao(ILFlightDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushSession() {
        dao.flushSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSession() {
        dao.clearSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(Object entity) {
        dao.evict(entity);
    }

    /**
     * Set the ApplicationContext during object initialization.
     * @param applicationContext the ApplicationContext object
     * @throws BeansException if any
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogMessage createLogMessage(LogMessage entity) throws RAException {
        return dao.createLogMessage(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Division getDivision(String name) throws RAException {
        return dao.getDivision(name);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Division getDivisionFull(String name) throws RAException {
        return dao.getDivisionFull(name);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Squadron getSquadron(Long divisionId, String name) throws RAException {
        return dao.getSquadron(divisionId, name);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public Division createDivision(String name) throws RAException {
        return dao.createDivision(name);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public Squadron createSquadron(Long divisionId, String name) throws RAException {
        return dao.createSquadron(divisionId, name);
    }

    /**
     * {@inheritDoc}
     */
    public Map<Long, Person> getPersonMap(Collection<Long> personIds) throws RAException {
        return dao.getPersonMap(personIds);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Person findPerson(String tabNum) throws RAException {
        return dao.findPerson(tabNum);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {Const.Cache.LOCAL, Const.Cache.PERSONS}, allEntries = true)
    @Override
    public Person createPerson(Person entity) throws RAException {
        return dao.createPerson(entity);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {Const.Cache.LOCAL, Const.Cache.PERSONS}, allEntries = true)
    @Override
    public Person updatePerson(Person entity) throws RAException {
        return dao.updatePerson(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trip> findTrips(String carrier, String flightNum, Timestamp flightDate, Timestamp actualDate,
                                String tailNum, String origin, String destination, String flightKind)
            throws RAException {
        return dao.findTrips(carrier, flightNum, flightDate, actualDate, tailNum, origin, destination, flightKind);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public Trip createTrip(Trip entity) throws RAException {
        return dao.createTrip(entity);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public Leg updateLeg(Leg entity) throws RAException {
        return dao.updateLeg(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Leg getLeg(Long id) throws RAException {
        return dao.getLeg(id);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public Leg updateLegAndScores(Leg entity, List<EventPrimaryParameter> evParameters,
                                  Map<Pair<Long, Short>, EventTypeScore> etsMap,
                                  Map<Long, EventType> eventTypeMap,
                                  Map<Short, Phase> phaseMap) throws RAException {
        Leg res = dao.linkLeg(entity);
        calculateScores(evParameters, etsMap, eventTypeMap, phaseMap);
        return res;
    }

    /**
     * Returns map of EventTypeScore entities. Map key is pair of event type and severity
     * @return map of EventTypeScore entities
     * @throws RAException if any
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Map<Pair<Long, Short>, EventTypeScore> getEventTypeScoresMap() throws RAException {
        return dao.getEventTypeScoresMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateScores(List<EventPrimaryParameter> evParameters,
                                Map<Pair<Long, Short>, EventTypeScore> etsMap,
                                Map<Long, EventType> eventTypeMap,
                                Map<Short, Phase> phaseMap) {
        for (EventPrimaryParameter epp : evParameters) {
            if (epp.getSeverityId() == 0) {
                //don't use INFO severity level in calculations
                continue;
            }
            Pair<Long, Short> key = new ImmutablePair<>(epp.getEventTypeId(), epp.getSeverityId());
            EventTypeScore typeScore = etsMap.get(key);
            if (typeScore != null) {
                EventScore score = new EventScore();
                score.setEventId(epp.getId());
                score.setEventTypeId(epp.getEventTypeId());
                score.setFlightId(epp.getFlightId());
                score.setPhaseId(epp.getPhaseId());
                score.setSeverityId(epp.getSeverityId());
                score.setEventTime(epp.getEventTime());
                score.setPrimaryParameterName(epp.getPrimaryParameterName());
                score.setValue(epp.getParameterValue());
                score.setAircraftId(epp.getAircraftId());
                score.setAirportIdTakeOff(epp.getAirportIdTakeOff());
                score.setAirportIdLanding(epp.getAirportIdLanding());
                score.setScore(typeScore.getScore());
                score.setFlightDate(epp.getFlightTime());
                EventType et = eventTypeMap.get(epp.getEventTypeId());
                if (et != null) {
                    score.setEventTypeName(et.getName());
                }
                Phase phase = phaseMap.get(epp.getPhaseId());
                if (phase != null) {
                    score.setPhaseCode(phase.getCode());
                }
                dao.save("EventScore", score);
            } else {
                Map<String, String> props = new HashMap<>();
                props.put("type", String.valueOf(epp.getEventTypeId()));
                props.put("severity", String.valueOf(epp.getSeverityId()));
                appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                        Const.LogClassifier.EVT_CALC, Const.LogCode.CFTS_MISSING, Level.WARN, props)));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> findPersons(PersonSearchCondition condition) throws RAException {
        return dao.findPersons(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public List<Division> findDivisions() throws RAException {
        return dao.findDivisions();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Map<Long, DivisionRole> getDivisionRolesMap() throws RAException {
        return dao.getDivisionRolesMap();
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public DivisionRole saveDivisionRole(DivisionRole entity) throws RAException {
        return dao.saveDivisionRole(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Map<Long, Squadron> findSquadronMap() throws RAException {
        Map<Long, Squadron> res = new HashMap<>();
        List<Division> divisions = dao.findDivisions();
        for (Division d : divisions) {
            for (Squadron s : d.getSquadrons()) {
                res.put(s.getId(), s);
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Map<String, List<String>> getAircraftRegNumMap() throws RAException {
        return dao.getAircraftRegNumMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Leg> findUnlinkedLegs(Timestamp dueDate) throws RAException {
        return dao.findUnlinkedLegs(dueDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Leg> findUnlinkedLegs(Date from, Date to) throws RAException {
        return dao.findUnlinkedLegs(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> findPersonsForLegs(Collection<Long> legIds) throws RAException {
        return dao.findPersonsForLegs(legIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PersonsSumByMonth> findSquadronPersonSumLegs(Long squadronId) throws RAException {
        return dao.findSquadronPersonSumLegs(squadronId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countOldUnlinkedLegs(Timestamp upDate) throws RAException {
        return dao.countOldUnlinkedLegs(upDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AirportCode> findAirportCodeByAllCodes(AirportCode value) throws RAException {
        return dao.findAirportCodeByAllCodes(value);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LOCAL)
    @Override
    public Map<String, AirportCode> getAirportCodesMap()  throws RAException {
        return dao.getAirportCodesMap();
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public AirportCode createAirportCode(AirportCode entity) throws RAException {
        return dao.createAirportCode(entity);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = Const.Cache.LOCAL, allEntries = true)
    @Override
    public AirportCode updateAirportCode(AirportCode entity) throws RAException {
        return dao.updateAirportCode(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadedFlight createLoadedFlight(LoadedFlight entity) throws RAException {
        return dao.createLoadedFlight(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LoadedFlight> findLoadedFlights(String fileName) throws RAException {
        return dao.findLoadedFlights(fileName);
    }

    /**
     * Returns hibernate statistics. type is comparison type. sign define comparison sign - > or <.
     * Values of specified type compares with level, according to sign, and added to result.
     * min, +1, 5 returns all statistics where ExecutionMinTime > 5 ms.
     * @param type type of statistics - max - ExecutionMaxTime, min - ExecutionMinTime,
     * avg - ExecutionAvgTime, count - ExecutionCount
     * @param sign - sign of comparison,  {+1, -1}
     * @param level - comparison level
     * @return hibernate statistics.
     */
    @Override
    public Map<String, QueryStatistics> getQueryStatistics(String type, Integer sign, Long level) {
        Statistics stat = dao.getSessionFactory().getStatistics();
        if (stat.isStatisticsEnabled()) {
            Map<String, QueryStatistics> res = new HashMap<>();
            String[] queries = stat.getQueries();
            for (String query : queries) {
                QueryStatistics qs = stat.getQueryStatistics(query);
                if ("max".equalsIgnoreCase(type)) {
                    if (compare(sign, level, qs.getExecutionMaxTime())) {
                        res.put(query, qs);
                    }
                }
                else if ("avg".equalsIgnoreCase(type)) {
                    if (compare(sign, level, qs.getExecutionAvgTime())) {
                        res.put(query, qs);
                    }
                }
                else if ("min".equalsIgnoreCase(type)) {
                    if (compare(sign, level, qs.getExecutionMinTime())) {
                        res.put(query, qs);
                    }
                }
                else if ("count".equalsIgnoreCase(type)) {
                    if (compare(sign, level, qs.getExecutionCount())) {
                        res.put(query, qs);
                    }
                }
            }
            return res;
        }
        else return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventScore> findIncompleteEventScores(int maxResults, long firstId) throws RAException {
        return dao.findIncompleteEventScores(maxResults, firstId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventScore updateEventScore(EventScore entity) throws RAException {
        return dao.updateEventScore(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TopStat> findTopRoutesStat(Long personId, Date from, Integer count) throws RAException {
        return dao.findTopRoutesStat(personId, from, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TopStat> findTopPlanesStat(Long personId, Date from, Integer count) throws RAException {
        return dao.findTopPlanesStat(personId, from, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TopStat> findPilotTopCrewStat(Long personId, Date from, Integer count) throws RAException {
        return dao.findPilotTopCrewStat(personId, from, count);
    }

    private boolean compare(int sign, long level, long value) {
        if (sign > 0) {
            return (value >= level);
        }
        else {
            return (value < level);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto tryLogin(Long personId, String password) throws RAException {
        UserDto user = dao.getUserByPerson(personId);
        if (user == null) {
            return null;
        }
        if (StringUtils.equals(user.getPassword(), password)) {
            return user;
        }
        else return null;
    }

}
