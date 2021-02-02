package cselp.dao.external;

import cselp.bean.LegSearchCondition;
import cselp.dao.DataByIdsFiller;
import cselp.dao.GenericDao;
import cselp.domain.external.*;
import cselp.domain.local.AirportCode;
import cselp.domain.local.Leg;
import cselp.exception.RADbException;
import cselp.exception.RAException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;

import java.sql.Timestamp;
import java.util.*;


public class FlightDao extends GenericDao implements IFlightDao {
    private static final Log log = LogFactory.getLog(FlightDao.class);

    public static final int MAX_LIST_RESULTS = 100;

    private Properties appConfig;

    public void setAppConfig(Properties appConfig) {
        this.appConfig = appConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flight getFlight(long id) throws RAException {
        try {
            return get("Flight", id);
        } catch (Exception ex) {
            log.error("Error in getFlight.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Flight> getFlights(Collection<Long> ids) throws RAException {
        try {
            List<Flight> res = new ArrayList<>();
            FlightFiller filler = new FlightFiller(1000, ids);
            while (filler.hasNext()) {
                res.addAll(filler.next());
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
    public List<EventPrimaryParameter> findEventPrimaryParametersByFlight(Long flightId) throws RAException {
        try {
            return findByNamedQueryAndNamedParam("findEventPrimaryParametersByFlight", "flightId", flightId);
        }
        catch (Exception ex) {
            log.error("Error in findEventPrimaryParametersByFlight.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Flight> findSuitableFlight(LegSearchCondition condition, Map<String, List<String>> regNumMap)
            throws RAException {
        try {
            if (CollectionUtils.isEmpty(condition.getDestinationIds()) ||
                    CollectionUtils.isEmpty(condition.getOriginIds())) {
                log.info("Leg " + condition.getId() + " : Destination or Origin is empty.");
                return new ArrayList<>();
            }
            String delta = appConfig.getProperty("flight.times.delta");
            long deltaInMillis = 0;
            try {
                deltaInMillis = Integer.parseInt(delta) * 1000;
                if (log.isDebugEnabled()) {
                    log.debug("flight sync delta time = " + deltaInMillis);
                }
            }
            catch (Exception e) {
                log.error("Error parsing delta " + delta, e);
            }
            Timestamp departure = new Timestamp(condition.getDepartureFact().getTime() - deltaInMillis);
            Timestamp takeOff = new Timestamp(condition.getTakeOff().getTime() + deltaInMillis);
            Timestamp arrival = new Timestamp(condition.getArrivalFact().getTime() + deltaInMillis);
            Timestamp touchDown = new Timestamp(condition.getTouchDown().getTime() - deltaInMillis);
            String[] params = {"departure", "takeOff", "touchDown", "arrival"};
            Object[] args = {departure, takeOff, touchDown, arrival};
            String hql = "select f from Flight f " +
                    " where f.aircraftRegNum=:tailNum " +
                    " and f.startDate >= :departure and f.startDate <= :takeOff " +
                    " and f.endDate >= :touchDown and f.endDate <= :arrival " +
                    " and f.aptIdLanding in (:aptIdLanding) and f.aptIdTakeOff in (:aptIdTakeOff) ";
            Query q = sessionFactory.getCurrentSession().createQuery(hql);
            for (int i = 0; i < params.length; i++) {
                q.setParameter(params[i], args[i]);
            }
            q.setParameterList("aptIdLanding", condition.getDestinationIds());
            q.setParameterList("aptIdTakeOff", condition.getOriginIds());
            return queryWithAlternateParameters(condition, regNumMap, q);
        } catch (Exception ex) {
            log.error("Error in findSuitableFlight.", ex);
            throw new RADbException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Flight> queryWithAlternateParameters(LegSearchCondition condition,
                                                      Map<String, List<String>> regNumMap, Query q) {
        q.setParameter("tailNum", condition.getTailNum());
        List<Flight> res = q.list();
        if (!res.isEmpty()) {
            return res;
        }
        List<String> altRegNums = regNumMap.get(condition.getTailNum());
        if (altRegNums != null) {
            for (String altRegNum : altRegNums) {
                q.setParameter("tailNum", altRegNum);
                res = q.list();
                if (!res.isEmpty()) {
                    return res;
                }
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FlightWrapper> findSimilarFlight(Leg condition, Map<String, List<String>> regNumMap, Long delta)
            throws RADbException {
         try {
             //changed delta times
             //search by times exclude tail-no or airports
             //exclude origin or departure
             long deltaInMillis = getDeltaInMillis(delta);
             String[] params = {};
             Object[] args = {};
             String hql = "select f from FlightWrapper f, Airport aptLand, Airport aptTakeOff " +
                     " where aptLand.id = f.aptIdLanding and aptTakeOff.id = f.aptIdTakeOff ";
             if (condition.getTailNum() != null) {
                 hql += " and f.aircraftRegNum=:tailNum ";
                 params = ArrayUtils.add(params, "tailNum");
                 args = ArrayUtils.add(args, condition.getTailNum());
             }
             if (condition.getDepartureFact() != null && condition.getTakeOff() != null) {
                 hql += " and f.startDate >= :departure and f.startDate <= :takeOff ";
                 Timestamp departure = new Timestamp(condition.getDepartureFact().getTime() - deltaInMillis);
                 Timestamp takeOff = new Timestamp(condition.getTakeOff().getTime() + deltaInMillis);
                 params = ArrayUtils.addAll(params, "departure", "takeOff");
                 args = ArrayUtils.addAll(args, departure, takeOff);
             }
             if (condition.getArrivalFact() != null && condition.getTouchDown() != null) {
                 hql += " and f.endDate >= :touchDown and f.endDate <= :arrival ";
                 Timestamp arrival = new Timestamp(condition.getArrivalFact().getTime() + deltaInMillis);
                 Timestamp touchDown = new Timestamp(condition.getTouchDown().getTime() - deltaInMillis);
                 params = ArrayUtils.addAll(params, "touchDown", "arrival");
                 args = ArrayUtils.addAll(args, touchDown, arrival);
             }
             if (condition.getOriginIcao() != null) {
                 hql += " and aptTakeOff.icaoCode = :originIcao ";
                 params = ArrayUtils.add(params, "originIcao");
                 args = ArrayUtils.add(args, condition.getOriginIcao());
             }
             if (condition.getOriginIata() != null) {
                 hql += " and aptTakeOff.iataCode = :originIata";
                 params = ArrayUtils.add(params, "originIata");
                 args = ArrayUtils.add(args, condition.getOriginIata());
             }
             if (condition.getDestinationIcao() != null) {
                 hql += " and aptLand.icaoCode = :destinationIcao ";
                 params = ArrayUtils.add(params, "destinationIcao");
                 args = ArrayUtils.add(args, condition.getDestinationIcao());
             }
             if (condition.getDestinationIata() != null) {
                 hql += " and aptLand.iataCode = :destinationIata ";
                 params = ArrayUtils.add(params, "destinationIata");
                 args = ArrayUtils.add(args, condition.getDestinationIata());
             }
             Query q = sessionFactory.getCurrentSession().createQuery(hql);
             for (int i = 0; i < params.length; i++) {
                 q.setParameter(params[i], args[i]);
             }
             q.setMaxResults(MAX_LIST_RESULTS);
             List<FlightWrapper> result = new ArrayList<>();
             result.addAll(q.list());
             //search all variants for conditions
             if (condition.getTailNum() != null) {
                 List<String> altRegNums = regNumMap.get(condition.getTailNum());
                 if (altRegNums != null) {
                     for (String altRegNum : altRegNums) {
                         args[0] = altRegNum;
                         q.setParameter(params[0], args[0]);
                         result.addAll(q.list());
                         //search all variants
                     }
                 }
             }
             return result;
         } catch (Exception ex) {
             log.error("Error in findSimilarFlight.", ex);
             throw new RADbException(ex);
         }
    }

    private long getDeltaInMillis(Long delta) {
        long deltaInMillis = 0;
        if (delta == null) {
            String sDelta = appConfig.getProperty("flight.times.delta");
            try {
                deltaInMillis = Integer.parseInt(sDelta) * 1000;
                log.info("flight sync delta time = " + deltaInMillis);
            } catch (Exception e) {
                log.error("Error parsing delta " + sDelta, e);
            }
        }
        else {
            deltaInMillis = delta;
        }
        return deltaInMillis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AirportCode> findAllAirFASEAptCodes() throws RAException {
        try {
            return findByNamedQuery("findAllAirFASEAptCodes");
        } catch (Exception ex) {
            log.error("Error in findAllAirFASEAptCodes.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Aircraft> getAircraftMap() throws RAException {
        try {
            Map<Long, Aircraft> res = new HashMap<>();
            List<Aircraft> list = findByNamedQuery("findAllAircraft");
            for (Aircraft a : list) {
                res.put(a.getId(), a);
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getAircraftMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, EventType> getEventTypesMap() throws RAException {
        try {
            Map<Long, EventType> res = new HashMap<>();
            List<EventType> list = findByNamedQuery("findAllEventTypes");
            for (EventType et : list) {
                res.put(et.getId(), et);
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getEventTypesMap.", ex);
            throw new RADbException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Short, Phase> getPhaseMap() throws RAException {
        try {
            Map<Short, Phase> res = new HashMap<>();
            List<Phase> list = findByNamedQuery("findAllPhases");
            for (Phase p : list) {
                res.put(p.getId(), p);
            }
            return res;
        } catch (Exception ex) {
            log.error("Error in getPhaseMap.", ex);
            throw new RADbException(ex);
        }
    }

    private class FlightFiller extends DataByIdsFiller<Flight> {

        public FlightFiller(int batchSize, Collection<Long> ids) {
            super(batchSize, ids);
        }

        @Override
        protected Collection<Flight> getQueryResult(Collection<Long> batchIds) {
            return findByNamedQueryAndNamedParam("findFlightByIds", "ids", batchIds);
        }
    }

}
