package cselp.service.external;


import cselp.Const;
import cselp.bean.AirportCodesContainer;
import cselp.bean.LegSearchCondition;
import cselp.dao.external.IFlightDao;
import cselp.domain.external.*;
import cselp.domain.local.AirportCode;
import cselp.domain.local.Leg;
import cselp.exception.RADbException;
import cselp.exception.RAException;
import cselp.util.ConvertUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;

public class FlightService implements IFlightService {
    private static final Log log = LogFactory.getLog(FlightService.class);

    private IFlightDao dao;

    public void setDao(IFlightDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flight getFlight(Long id) throws RAException {
        return dao.getFlight(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Flight> getFlights(Collection<Long> ids) throws RAException {
        return dao.getFlights(ids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventPrimaryParameter> findEventPrimaryParametersByFlight(Long flightId) throws RAException {
        return dao.findEventPrimaryParametersByFlight(flightId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Flight> findSuitableFlight(LegSearchCondition cond, Map<String, List<String>> regNumMap)
            throws RAException {
        return dao.findSuitableFlight(cond, regNumMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FlightWrapper> findSimilarFlight(Leg condition, Map<String, List<String>> regNumMap, Long delta)
            throws RADbException {
        return dao.findSimilarFlight(condition, regNumMap, delta);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.EXTERNAL)
    @Override
    public List<AirportCode> findAllAirFASEAptCodes() throws RAException {
        return dao.findAllAirFASEAptCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.EXTERNAL)
    @Override
    public Map<Long, Aircraft> getAircraftMap() throws RAException {
        return dao.getAircraftMap();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.EXTERNAL)
    @Override
    public Map<String, Aircraft> getTailAircraftMap() throws RAException {
        Collection<Aircraft> acrList = dao.getAircraftMap().values();
        Map<String, Aircraft> res = new HashMap<>();
        for (Aircraft a : acrList) {
            res.put(a.getRegistrationNum(), a);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.EXTERNAL)
    @Override
    public Map<Long, EventType> getEventTypesMap() throws RAException {
        return dao.getEventTypesMap();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.EXTERNAL)
    @Override
    public Map<Short, Phase> getPhaseMap() throws RAException {
        return dao.getPhaseMap();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = Const.Cache.LONG_EXTERNAL)
    @Override
    public AirportCodesContainer getAirportCodes() throws RAException {
        AirportCodesContainer res = new AirportCodesContainer();
        List<AirportCode>  codes = dao.findAllAirFASEAptCodes();
        for (AirportCode code : codes) {
            if (code.getIcaoCode() != null) {
                if (code.getIataCode() != null) {
                    res.getCodesToAptMap().put(code.getIcaoCode() + code.getIataCode(), code.getId());
                }
                res.getIcaoToAptMap().put(code.getIcaoCode(), code.getId());
            }
            if (code.getIataCode() != null) {
                ConvertUtil.addToMapEntry(res.getIataToAptMap(), code.getIataCode(), code.getId());
            }
        }
        return res;
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
    public Map<String, QueryStatistics> getQueryStatistics(String type, int sign, long level) {
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

    private boolean compare(int sign, long level, long value) {
        if (sign > 0) {
            return (value >= level);
        }
        else {
            return (value < level);
        }
    }
}
