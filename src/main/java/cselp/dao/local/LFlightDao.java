package cselp.dao.local;

import cselp.bean.PersonSearchCondition;
import cselp.dao.DataByIdsFiller;
import cselp.dao.GenericDao;
import cselp.domain.local.*;
import cselp.exception.RADbException;
import cselp.exception.RAException;
import cselp.util.ConvertUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.*;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;

import java.sql.Timestamp;
import java.util.*;


public class LFlightDao extends GenericDao implements ILFlightDao {
    private static final Log log = LogFactory.getLog(LFlightDao.class);

    //maximum PrepareStatement parameters size
    public static final int MAX_PS_PARAMETERS = 1000;

    /**
     * {@inheritDoc}
     */
    @Override
    public LogMessage createLogMessage(LogMessage entity) throws RAException {
        try {
            return create("LogMessage", entity);
        } catch (Exception ex) {
            log.error("Error in createLogMessage.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Division getDivision(String name) throws RAException {
        try {
            return findUniqueResult("findDivision", params("name"), args(name));
        } catch (Exception ex) {
            log.error("Error in getDivision.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Division getDivisionFull(String name) throws RAException {
        try {
            return findUniqueResult("findDivisionFull", params("name"), args(name));
        } catch (Exception ex) {
            log.error("Error in getDivision.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Division createDivision(String name) throws RAException {
        try {
            Division div = new Division();
            div.setName(name);
            return create("Division", div);
        } catch (Exception ex) {
            log.error("Error in createDivision.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Squadron getSquadron(Long divisionId, String name) throws RAException {
        try {
            return findUniqueResult("findSquadron", params("divisionId", "name"), args(divisionId, name));
        } catch (Exception ex) {
            log.error("Error in getSquadron.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Squadron createSquadron(Long divisionId, String name) throws RAException {
        try {
            Squadron s  = new Squadron();
            s.setDivisionId(divisionId);
            s.setName(name);
            return create("Squadron", s);
        } catch (Exception ex) {
            log.error("Error in createSquadron.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, DivisionRole> getDivisionRolesMap() throws RAException {
        try {
            Map<Long, DivisionRole> res = new HashMap<>();
            List<DivisionRole> roles = findByNamedQuery("findDivisionRoles");
            for (DivisionRole role : roles) {
                res.put(role.getId(), role);
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getDivisionRolesMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DivisionRole saveDivisionRole(DivisionRole entity) throws RAException {
        try {
            return save("DivisionRole", entity);
        } catch (Exception ex) {
            log.error("Error in saveDivisionRole.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Person> getPersonMap(Collection<Long> personIds) throws RAException {
        try {
            Map<Long, Person> res = new HashMap<>();
            PersonFiller filler = new PersonFiller(MAX_PS_PARAMETERS, personIds);
            while (filler.hasNext()) {
                for (Person dto : filler.next()) {
                    res.put(dto.getId(), dto);
                }
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getPersonMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person findPerson(String tabNum) throws RAException {
        try {
            return findUniqueResult("findPersonByTabNum", params("tabNum"), args(tabNum));
        } catch (Exception ex) {
            log.error("Error in findPerson.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person createPerson(Person entity) throws RAException {
        try {
            Person res = create("Person", entity);
            if (res.getPersonMinimum() != null) {
                res.getPersonMinimum().setPersonId(res.getId());
                create("PersonMinimum", res.getPersonMinimum());
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in createPerson.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person updatePerson(Person entity) throws RAException {
        try {
            Person res = update("Person", entity);
            if (res.getPersonMinimum() != null) {
                res.getPersonMinimum().setPersonId(res.getId());
                update("PersonMinimum", res.getPersonMinimum());
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in updatePerson.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trip> findTrips(String carrier, String flightNum, Timestamp flightDate, Timestamp actualDate,
                          String tailNum, String origin, String destination, String flightKind)
            throws RAException {
        try {
            return findByNamedQueryAndNamedParam("findTrip",
                    params("carrier", "flightNum","flightDate", "actualDate", "tailNum",
                            "origin", "destination", "flightKind"),
                    args(carrier, flightNum, flightDate, actualDate, tailNum, origin, destination, flightKind));
        } catch (Exception ex) {
            log.error("Error in findTrips.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Trip createTrip(Trip entity) throws RAException {
        try {
            Trip res = create("Trip", entity);
            for (Leg leg : res.getLegs()) {
                leg.setTripId(res.getId());
                leg = create("Leg", leg);
                for (Crew crew : leg.getCrews()) {
                    crew.setLegId(leg.getId());
                    crew = create("Crew", crew);
                    for (CrewMember cm : crew.getMembers()) {
                        cm.setCrewId(crew.getId());
                        cm = create("CrewMember", cm);
                    }
                }
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in createTrip.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Leg updateLeg(Leg entity) throws RADbException {
        try {
            return update("Leg", entity);
        } catch (Exception ex) {
            log.error("Error in updateLeg.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Leg linkLeg(Leg entity) throws RADbException {
        try {
            return update("LegLink", entity);
        } catch (Exception ex) {
            log.error("Error in linkLeg.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Leg getLeg(Long id) throws RADbException {
        try {
            return get("Leg", id);
        } catch (Exception ex) {
            log.error("Error in getLeg.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Leg> findUnlinkedLegs(Timestamp dueDate) throws RAException {
        try {
            return findByNamedQueryAndNamedParam("findUnlinkedLegs", "dueDate", dueDate);
        } catch (Exception ex) {
            log.error("Error in findUnlinkedLegs.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countOldUnlinkedLegs(Timestamp upDate) throws RAException {
        try {
            Query q = getNamedQueryWithNamedParam("countOldUnlinkedLegs", params("upDate"), args(upDate));
            return ((Number)q.uniqueResult()).longValue();
        } catch (Exception ex) {
            log.error("Error in countOldUnlinkedLegs.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Leg> findUnlinkedLegs(Date from, Date to) throws RAException {
        try {
            return findByNamedQueryAndNamedParam("findUnlinkedLegsRange", params("from", "to"), args(from, to));
        } catch (Exception ex) {
            log.error("Error in findUnlinkedLegsRange.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> findPersonsForLegs(Collection<Long> legIds) throws RAException {
        try {
            IdFiller filler = new IdFiller(MAX_PS_PARAMETERS, legIds, "findPersonIdsForLegs");
            List<Long> personIds = new ArrayList<>();
            while (filler.hasNext()) {
                personIds.addAll(filler.next());
            }
            PersonFiller pFiller = new PersonFiller(MAX_PS_PARAMETERS, personIds);
            List<Person> res = new ArrayList<>();
            while (pFiller.hasNext()) {
                res.addAll(pFiller.next());
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in findPersonLegs.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Person> findPersons(PersonSearchCondition condition) throws RAException {
        try {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria("Person", "person");
            if (condition.getSquadronId() != null) {
                crit.add(Restrictions.eq("squadronId", condition.getSquadronId()));
            }
            else if (condition.getDivisionId() != null) {
                List<Long> sqIds = findByNamedQueryAndNamedParam("findSquadronIdsForDivision",
                        "divisionId", condition.getDivisionId());
                if (!sqIds.isEmpty()) {
                    crit.add(Restrictions.in("squadronId", sqIds));
                }
            }
            if (condition.getLastName() != null) {
                crit.add(Restrictions.ilike("lastName", condition.getLastName(), MatchMode.START));
            }
            if (condition.getDivisionRole() != null) {
                crit.add(Restrictions.ilike("divisionRole", condition.getDivisionRole(), MatchMode.START));
            }
            crit.addOrder(Order.asc("fullName"));
            return (List<Person>)crit.list();
        } catch (Exception ex) {
            log.error("Error in findPersons.", ex);
            throw new RADbException(ex);
        }
    }

    @Override
    public List<Division> findDivisions() throws RAException {
        try {
            return findByNamedQuery("findDivisions");
        } catch (Exception ex) {
            log.error("Error in findDivisions.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<String>> getAircraftRegNumMap() throws RADbException {
        try {
            Map<String, List<String>> res = new HashMap<>();
            List<AircraftRegNumMap> list = findByNamedQuery("findAircraftRegNumMaps");
            for (AircraftRegNumMap regNumMap : list) {
                ConvertUtil.addToMapEntry(res, regNumMap.getRegNum(), regNumMap.getAlternateRegNum());
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getAircraftRegNumMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * Returns map of EventTypeScore entities. Map key is pair of event type and severity
     * @return map of EventTypeScore entities
     * @throws RADbException if any
     */
    @Override
    public Map<Pair<Long, Short>, EventTypeScore> getEventTypeScoresMap() throws RADbException {
        try {
            Map<Pair<Long, Short>, EventTypeScore> res = new HashMap<>();
            List<EventTypeScore> list = findByNamedQuery("getAllEventTypeScores");
            for (EventTypeScore ets : list) {
                Pair<Long, Short> key = new ImmutablePair<>(ets.getEventTypeId(), ets.getSeverityId());
                res.put(key, ets);
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getEventTypeScoresMap.", ex);
            throw new RADbException(ex);
        }
    }

    private class PersonFiller extends DataByIdsFiller<Person> {

        public PersonFiller(int batchSize, Collection<Long> ids) {
            super(batchSize, ids);
        }

        @Override
        protected Collection<Person> getQueryResult(Collection<Long> batchIds) {
            return findByNamedQueryAndNamedParam("findPersonByIds", "ids", batchIds);
        }
    }

    private class IdFiller extends DataByIdsFiller<Long> {
        private String queryName;
        public IdFiller(int batchSize, Collection<Long> ids, String queryName) {
            super(batchSize, ids);
            this.queryName = queryName;
        }

        @Override
        protected Collection<Long> getQueryResult(Collection<Long> batchIds) {
            return findByNamedQueryAndNamedParam(queryName, "ids", batchIds);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AirportCode> findAirportCodeByAllCodes(AirportCode value) throws RADbException {
        try {
            //use types to allow null values
            return findByNamedQueryAndNamedParam("findAirportCodeByAllCodes",
                    params("icaoCode", "iataCode"), args(value.getIcaoCode(), value.getIataCode()),
                    types(StringType.INSTANCE, StringType.INSTANCE));
        } catch (Exception ex) {
            log.error("Error in findAirportCodeByAllCodes.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, AirportCode> getAirportCodesMap()  throws RADbException {
        try {
            List<AirportCode> list = findByNamedQuery("findAllAirportCodes");
            Map<String, AirportCode> res = new HashMap<>();
            for (AirportCode ac : list) {
                res.put(ac.getCodes(), ac);
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getAirportCodesMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AirportCode createAirportCode(AirportCode entity) throws RADbException {
        try {
            return create("LAirportCode", entity);
        } catch (Exception ex) {
            log.error("Error in createAirportCode.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AirportCode updateAirportCode(AirportCode entity) throws RADbException {
        try {
            return update("LAirportCode", entity);
        } catch (Exception ex) {
            log.error("Error in updateAirportCode.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadedFlight createLoadedFlight(LoadedFlight entity) throws RADbException {
        try {
            return create("LoadedFlight", entity);
        } catch (Exception ex) {
            log.error("Error in createLoadedFlight.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LoadedFlight> findLoadedFlights(String fileName) throws RADbException {
        try {
            return findByNamedQueryAndNamedParam("findLoadedFlights", "fileName", fileName);
        } catch (Exception ex) {
            log.error("Error in findLoadedFlights.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<EventScore> findIncompleteEventScores(int maxResults, long firstId) throws RADbException {
        try {
            Query q = getNamedQueryWithNamedParam("findIncompleteEventScores",
                    params("firstId"), args(firstId));
            q.setMaxResults(maxResults);
            return q.list();
        } catch (Exception ex) {
            log.error("Error in findIncompleteEventScores.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventScore updateEventScore(EventScore entity) throws RADbException {
        try {
            return update("EventScoreUpdater", entity);
        } catch (Exception ex) {
            log.error("Error in updateEventScore.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PersonsSumByMonth> findSquadronPersonSumLegs(Long squadronId) throws RAException {
        String sql = "select count(PERSON_ID) as persons, sum(summa) as sum, month, year from " +
                " (select m.PERSON_ID, count(*) as summa, MONTH(l.DEPARTURE_PLAN) as month, YEAR(l.DEPARTURE_PLAN) as year" +
                " from Leg l join CREW_TASK c on l.LEG_ID=c.LEG_ID join CREW_MEMBER m on c.CREW_TASK_ID=m.CREW_TASK_ID" +
                " where c.SQUADRON_ID = :squadronId " +
                " group by m.PERSON_ID, MONTH(l.DEPARTURE_PLAN), YEAR(l.DEPARTURE_PLAN))  as ss " +
                " group by month, year order by year, month";
        try {
            SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(sql);
            q.addScalar("persons", StandardBasicTypes.LONG);
            q.addScalar("sum", StandardBasicTypes.LONG);
            q.addScalar("month", StandardBasicTypes.INTEGER);
            q.addScalar("year", StandardBasicTypes.INTEGER);
            q.setParameter("squadronId", squadronId);
            q.setResultTransformer(new AliasToBeanResultTransformer(PersonsSumByMonth.class));
            return q.list();
        } catch (Exception ex) {
            log.error("Error in findSquadronSumLegs.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TopStat> findTopRoutesStat(Long personId, Date from, Integer count) throws RAException {
        try {
            Query q = getNamedQueryWithNamedParam("findTopRoutesStat", params("personId", "from"),
                    args(personId, from));
            q.setResultTransformer(new AliasToBeanResultTransformer(TopStat.class));
            if (count != null) {
                q.setMaxResults(count);
            }
            return q.list();
        } catch (Exception ex) {
            log.error("Error in findTopRoutesStat.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TopStat> findTopPlanesStat(Long personId, Date from, Integer count) throws RAException {
        try {
            Query q = getNamedQueryWithNamedParam("findTopPlanesStat", params("personId", "from"),
                    args(personId, from));
            q.setResultTransformer(new AliasToBeanResultTransformer(TopStat.class));
            if (count != null) {
                q.setMaxResults(count);
            }
            return q.list();
        } catch (Exception ex) {
            log.error("Error in findTopPlanesStat.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TopStat> findPilotTopCrewStat(Long personId, Date from, Integer count) throws RAException {
        try {
            SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(
                    "select cm2.PERSON_ID as personId, count(*) as count from " +
                    " (select cm2.PERSON_ID from CREW_MEMBER cm, CREW_MEMBER cm2, CREW_TASK ct, LEG l " +
                            " where cm.CREW_TASK_ID=cm2.CREW_TASK_ID and cm.CREW_TASK_ID=ct.CREW_TASK_ID and ct.LEG_ID=l.LEG_ID " +
                            " and cm.PERSON_ID = :id and l.DEPARTURE_PLAN >= :from) as cm2 " +
                    " group by cm2.PERSON_ID order by count(*) desc");
            q.addScalar("personId", StandardBasicTypes.LONG);
            q.addScalar("count", StandardBasicTypes.LONG);
            q.setParameter("id", personId);
            q.setParameter("from", from);
            q.setResultTransformer(new AliasToBeanResultTransformer(TopStat.class));
            if (count != null) {
                q.setMaxResults(count);
            }
            return q.list();
        } catch (Exception ex) {
            log.error("Error in findTopFlightRolesStat.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto getUserByPerson(Long personId) throws RAException {
        try {
            return findUniqueResult("findUserByPerson", params("personId"), args(personId));
        } catch (Exception ex) {
            log.error("Error in getUserByPerson.", ex);
            throw new RADbException(ex);
        }
    }
}
