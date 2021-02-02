package cselp.dao.local;

import cselp.Const;
import cselp.bean.FlightSearchCondition;
import cselp.dao.DataByIdsFiller;
import cselp.dao.GenericDao;
import cselp.domain.local.Crew;
import cselp.domain.local.EventScore;
import cselp.domain.local.Leg;
import cselp.domain.local.Trip;
import cselp.domain.report.PersonFlight;
import cselp.domain.report.PersonLeg;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class ReportDao extends GenericDao implements IReportDao {
    private static final Log log = LogFactory.getLog(ReportDao.class);

    public static final int MAX_PS_PARAMETERS = 1000;

    public static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PersonFlight> findPersonFlights(Long personId) throws RAException {
        try {
            List<PersonFlight> list = findByNamedQueryAndNamedParam("findPersonFlights", "personId", personId);
            Set<Long> flightIds = new HashSet<>();
            for (PersonFlight pf : list) {
                if (pf.getFlightId() != null) {
                    flightIds.add(pf.getFlightId());
                }
            }
            ScoreFiller filler = new ScoreFiller(MAX_PS_PARAMETERS, flightIds);
            Map<Long, List<EventScore>> scoresMap = new HashMap<>();
            while (filler.hasNext()) {
                for (EventScore dto : filler.next()) {
                    ConvertUtil.addToMapEntry(scoresMap, dto.getFlightId(), dto);
                }
            }
            for (PersonFlight pf : list) {
                List<EventScore> scoreList = scoresMap.get(pf.getFlightId());
                if (scoreList != null) {
                    pf.getScores().addAll(scoreList);
                }
            }
            return list;
        } catch (Exception ex) {
            log.error("Error in findPersonFlights.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, List<EventScore>> findEventScoresMap(Collection<Long> flightIds) throws RAException {
        try {
            Map<Long, List<EventScore>> scoresMap = new HashMap<>();
            ScoreFiller filler = new ScoreFiller(MAX_PS_PARAMETERS, flightIds);
            while (filler.hasNext()) {
                for (EventScore dto : filler.next()) {
                    ConvertUtil.addToMapEntry(scoresMap, dto.getFlightId(), dto);
                }
            }
            return scoresMap;
        } catch (Exception ex) {
            log.error("Error in findEventScoresMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, List<PersonLeg>> findPersonLegsOpt(FlightSearchCondition condition) throws RAException {
        try {
            int maxResults = 30;
            if (condition.getMaxResults() != null && condition.getMaxResults() > 0) {
                maxResults = condition.getMaxResults();
            }
            String withPrefix = "WITH EV AS (SELECT SUM(CASE WHEN SEV_ID = 1 THEN 1 ELSE 0 END) LOW, " +
                    " SUM(CASE WHEN SEV_ID = 2 THEN 1 ELSE 0 END) MEDIUM, " +
                    "SUM(CASE WHEN SEV_ID = 3 THEN 1 ELSE 0 END) HIGH , FLI_ID " +
                    "FROM EVENT_SCORES GROUP BY FLI_ID) ";
            String tables = "LEG L, CREW_TASK CT, CREW_MEMBER CM";
            String where = "CT.LEG_ID=L.LEG_ID AND CM.CREW_TASK_ID=CT.CREW_TASK_ID ";
            if (condition.getSeverity() != null && !condition.getSeverity().isEmpty()) {
                tables += ", EV";
                where += "AND EV.FLI_ID=L.FLIGHT_ID ";
            }
            String cntQuery = "SELECT count(*) cnt FROM " + tables +
                    " WHERE " + where;
            String query = "SELECT TOP(" + maxResults + ") L.LEG_ID as legId FROM " + tables +
                    " WHERE " + where;
            if (condition.getSeverity() != null && !condition.getSeverity().isEmpty()) {
                cntQuery = withPrefix + cntQuery;
                query = withPrefix + query;
            }
            long start = System.nanoTime();
            if (condition.getFrom() != null) {
                cntQuery += " and L.DEPARTURE_FACT >= '" + df.get().format(condition.getFrom()) + "'";
                query += " and L.DEPARTURE_FACT >= '" + df.get().format(condition.getFrom()) + "'";
            }
            if (condition.getTo() != null) {
                cntQuery += " and L.DEPARTURE_FACT < '" + df.get().format(condition.getTo()) + "'";
                query += " and L.DEPARTURE_FACT < '" + df.get().format(condition.getTo()) + "'";
            }
            if (condition.getOriginIcao() != null) {
                cntQuery += " and L.ORIGIN_ICAO='" + condition.getOriginIcao() + "'";
                query += " and L.ORIGIN_ICAO='" + condition.getOriginIcao() + "'";
            }
            if (condition.getDestinationIcao() != null) {
                cntQuery += " and L.DESTINATION_ICAO='" + condition.getDestinationIcao() + "'";
                query += " and L.DESTINATION_ICAO='" + condition.getDestinationIcao() + "'";
            }
            Long personId = null;
            if (condition.getPersonIds() != null && !condition.getPersonIds().isEmpty()) {
                personId = condition.getPersonIds().get(0);
                cntQuery += " and CM.PERSON_ID = " + personId;
                query += " and CM.PERSON_ID = " + personId;
            }
            if (condition.getSeverity() != null && !condition.getSeverity().isEmpty()) {
                if (condition.getSeverity().contains(Const.EventSeverity.LOW)) {
                    cntQuery += " and EV.LOW > 0";
                    query += " and EV.LOW > 0";
                }
                if (condition.getSeverity().contains(Const.EventSeverity.MEDIUM)) {
                    cntQuery += " and EV.MEDIUM > 0";
                    query += " and EV.MEDIUM > 0";
                }
                if (condition.getSeverity().contains(Const.EventSeverity.HIGH)) {
                    cntQuery += " and EV.HIGH > 0";
                    query += " and EV.HIGH > 0";
                }
            }
            query += " order by L.DEPARTURE_FACT desc";
            /*SQLQuery cntQ = sessionFactory.getCurrentSession().createSQLQuery(cntQuery);
            cntQ.addScalar("cnt", StandardBasicTypes.LONG);
            long size = ((Number) cntQ.uniqueResult()).longValue();
            String timing = "findPersonLegsOpt timing : find count[" + size + "] - " + (System.nanoTime() - start)/1000000;
            start = System.nanoTime();*/
            long size = 0;
            SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(query);
            q.addScalar("legId", StandardBasicTypes.LONG);
            List<Long> legIds = q.list();
            //timing += ", findPersonLegs Ids[" + legIds.size() + "] - " + (System.nanoTime() - start)/1000000;
            String timing = "findPersonLegsOpt timing : findPersonLegs Ids[" + legIds.size() + "] - " +
                    (System.nanoTime() - start)/1000000;
            start = System.nanoTime();
            List<PersonLeg> res;
            if (legIds.isEmpty()) {
                res = new ArrayList<>();
            }
            else {
                res = findByNamedQueryAndNamedParam("findLegsByLegIds", "legIds", legIds);
                timing += ", findPersonLegs[" + res.size() + "] - " + (System.nanoTime() - start) / 1000000;
                start = System.nanoTime();
                //append data from flights, crews, scores
                Set<Long> tripIds = new HashSet<>();
                for (PersonLeg pl : res) {
                    tripIds.add(pl.getOsFlightId());
                }
                List<Trip> trips = findByNamedQueryAndNamedParam("findRTripLightByIds", "ids", tripIds);
                Map<Long, Trip> tripMap = new HashMap<>();
                for (Trip t : trips) {
                    tripMap.put(t.getId(), t);
                }
                timing += ", find flights[" + trips.size() + "] - " + (System.nanoTime() - start)/1000000;
                start = System.nanoTime();
                //set crews
                Map<Long, List<Crew>> crewMap = new HashMap<>();
                CrewFiller filler = new CrewFiller(MAX_PS_PARAMETERS, legIds);
                while (filler.hasNext()) {
                    for (Crew dto : filler.next()) {
                        ConvertUtil.addToMapEntry(crewMap, dto.getLegId(), dto);
                    }
                }
                Map<Long, PersonLeg> legMap = new HashMap<>();
                for (PersonLeg pl : res) {
                    pl.setPersonId(personId);
                    //flight data
                    Trip t = tripMap.get(pl.getOsFlightId());
                    addTripData(pl, t);
                    //crew data
                    List<Crew> crews = crewMap.get(pl.getLegId());
                    pl.getCrews().addAll(crews);
                    legMap.put(pl.getLegId(), pl);
                }
                //sort legs
                List<PersonLeg> sorted = new ArrayList<>();
                for (Long legId : legIds) {
                    PersonLeg pl = legMap.get(legId);
                    if (pl != null) {
                        sorted.add(pl);
                    }
                }
                res = sorted;
                timing += ", link flights, crews - " + (System.nanoTime() - start)/1000000;
            }
            log.info(timing);
            return new ImmutablePair<>(size, res);
        } catch (Exception ex) {
            log.error("Error in findPersonLegsOpt", ex);
            throw new RADbException(ex);
        }
    }

    private static void addTripData(PersonLeg l, Trip t) {
        if (t != null) {
            l.setCarrier(t.getCarrier());
            l.setFlightNo(t.getFlightNum());
            l.setFlightDate(t.getFlightDate());
            l.setActualDate(t.getActualDate());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Pair<Long, List<Trip>> findTrips(FlightSearchCondition condition) throws RAException {
        try {
            Criteria c = sessionFactory.getCurrentSession().createCriteria("RTrip", "trip");
            if (condition.getFrom() != null) {
                c.add(Restrictions.ge("trip.flightDate", condition.getFrom()));
            }
            if (condition.getTo() != null) {
                c.add(Restrictions.lt("trip.flightDate", condition.getTo()));
            }
            c.createAlias("trip.legs", "leg");
            if (condition.getOriginIcao() != null) {
                c.add(Restrictions.eq("leg.originIcao", condition.getOriginIcao()));
            }
            if (condition.getDestinationIcao() != null) {
                c.add(Restrictions.eq("leg.destinationIcao", condition.getDestinationIcao()));
            }
            c.createAlias("leg.crews", "crew");
            c.createAlias("crew.members", "member");
            if (condition.getFlightRole() != null) {
                c.add(Restrictions.eq("member.flightRole", condition.getFlightRole()));
            }
            if (condition.getPersonIds() != null && !condition.getPersonIds().isEmpty()) {
                c.add(Restrictions.in("member.personId", condition.getPersonIds()));
            }
            //c.createAlias() creates join, hibernate returns duplicated objects
            c.setProjection(Projections.rowCount());
            long size = ((Number)c.uniqueResult()).longValue();
            c.setProjection(null);
            if (condition.getOrder() != null) {
                c.addOrder(condition.getOrder() ? Order.asc("trip.flightDate") : Order.desc("trip.flightDate"));
            }
            c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            if (condition.getFirstResult() != null && condition.getFirstResult() > 0) {
                c.setFirstResult(condition.getFirstResult());
            }
            if (condition.getMaxResults() != null && condition.getMaxResults() > 0) {
                c.setMaxResults(condition.getMaxResults());
            }
            List<Trip> res = c.list();
            return new ImmutablePair<>(size, res);
        } catch (Exception ex) {
            log.error("Error in findFlights.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trip> findTrips(Date from, Date to) throws RAException {
        try {
            return findByNamedQueryAndNamedParam("findRTripRange",
                    params("from", "to"), args(from, to));
        } catch (Exception ex) {
            log.error("Error in findTrips.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Leg getLeg(Long id) throws RADbException {
        try {
            return get("RLeg", id);
        } catch (Exception ex) {
            log.error("Error in getLeg.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Trip> getTripMap(Collection<Long> tripIds) throws RAException {
        try {
            Map<Long, Trip> res = new HashMap<>();
            TripFiller filler = new TripFiller(MAX_PS_PARAMETERS, tripIds);
            while (filler.hasNext()) {
                for (Trip dto : filler.next()) {
                    res.put(dto.getId(), dto);
                }
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getTripMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Leg> findLastLegs(Long personId, Integer count) throws RAException {
        try {
            Query q = getNamedQueryWithNamedParam("findRLastLegs", params("personId"), args(personId));
            if (count != null) {
                q.setMaxResults(count);
            }
            return q.list();
        } catch (Exception ex) {
            log.error("Error in findLastLegs.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Leg> findPersonLegs(Long personId) throws RAException {
        try {
            return findByNamedQueryAndNamedParam("findRPersonLegs", "personId", personId);
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
    public List<EventScore> findPersonEventScores(Long personId) throws RADbException {
        try {
            Query q = getNamedQueryWithNamedParam("findRPersonEventScores",
                    params("personId"), args(personId));
            return q.list();
        } catch (Exception ex) {
            log.error("Error in findPersonEventScores.", ex);
            throw new RADbException(ex);
        }
    }

    private class ScoreFiller extends DataByIdsFiller<EventScore> {

        public ScoreFiller(int batchSize, Collection<Long> ids) {
            super(batchSize, ids);
        }

        @Override
        protected Collection<EventScore> getQueryResult(Collection<Long> batchIds) {
            return findByNamedQueryAndNamedParam("findREventScoresByFlightIds", "flightIds", batchIds);
        }
    }

    private class CrewFiller extends DataByIdsFiller<Crew> {

        public CrewFiller(int batchSize, Collection<Long> ids) {
            super(batchSize, ids);
        }

        @Override
        protected Collection<Crew> getQueryResult(Collection<Long> batchIds) {
            return findByNamedQueryAndNamedParam("findRCrewsByLegIds", "legIds", batchIds);
        }
    }

    private class TripFiller extends DataByIdsFiller<Trip> {

        public TripFiller(int batchSize, Collection<Long> ids) {
            super(batchSize, ids);
        }

        @Override
        protected Collection<Trip> getQueryResult(Collection<Long> batchIds) {
            return findByNamedQueryAndNamedParam("findRTripLightByIds", "ids", batchIds);
        }
    }
}
