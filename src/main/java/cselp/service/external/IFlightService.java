package cselp.service.external;


import cselp.bean.AirportCodesContainer;
import cselp.bean.LegSearchCondition;
import cselp.domain.external.*;
import cselp.domain.local.AirportCode;
import cselp.domain.local.Leg;
import cselp.exception.RADbException;
import cselp.exception.RAException;
import org.hibernate.stat.QueryStatistics;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IFlightService {

    /**
     * Returns flight by specified identifier.
     * @param id flight identifier
     * @return flight object
     * @throws RAException if any
     */
    Flight getFlight(Long id) throws RAException;

    /**
     * Returns list of flights by specified identifiers.
     * @param ids collection of flight identifiers
     * @return list of flights
     * @throws RAException if any
     */
    List<Flight> getFlights(Collection<Long> ids) throws RAException;

    /**
     * Returns list of event's primary parameter by specified flight identifier.
     * @param flightId flight identifier
     * @return list of event's primary parameter
     * @throws RAException if any
     */
    List<EventPrimaryParameter> findEventPrimaryParametersByFlight(Long flightId) throws RAException;

    /**
     * Searches suitable flights according to search conditions.
     * @param cond leg search conditions
     * @param regNumMap maps the main reg number to alternate numbers.
     *                  An one airplane can be denoted by several reg numbers.
     * @return list of flights
     * @throws RAException  if any
     */
    List<Flight> findSuitableFlight(LegSearchCondition cond, Map<String, List<String>> regNumMap)
            throws RAException;

    /**
     * Searches flights according to search conditions.
     * Departure and takeoff flight times can differ from condition values on specified delta.
     * @param condition leg search conditions
     * @param regNumMap maps the main reg number to alternate numbers.
     *                  An one airplane can be denoted by several reg numbers.
     * @param delta time difference allowed
     * @return list of flights
     * @throws RADbException if any
     */
    List<FlightWrapper> findSimilarFlight(Leg condition, Map<String, List<String>> regNumMap, Long delta)
            throws RADbException;

    /**
     * Returns list of airport codes.
     * @return list of airport codes
     * @throws RAException if any
     */
    List<AirportCode> findAllAirFASEAptCodes() throws RAException;

    /**
     * Returns map aircraft identifier -> Aircraft bean
     * @return map of Aircrafts
     * @throws RAException if any
     */
    Map<Long, Aircraft> getAircraftMap() throws RAException;

    /**
     * Returns map aircraft reg number -> Aircraft bean
     * @return map of Aircrafts
     * @throws RAException if any
     */
    Map<String, Aircraft> getTailAircraftMap() throws RAException;

    /**
     * Returns map event type id -> event type
     * @return map of event types
     * @throws RAException if any
     */
    Map<Long, EventType> getEventTypesMap() throws RAException;

    /**
     * Returns map phase id -> flight phase
     * @return map of flight phases
     * @throws RAException if any
     */
    Map<Short, Phase> getPhaseMap() throws RAException;

    /**
     * Returns different presentations of airport codes: ICAO, IATA etc.
     * @return AirportCodesContainer  - container with airport codes mappings
     * @throws RAException if any
     */
    AirportCodesContainer getAirportCodes() throws RAException;

    /**
     * Returns hibernate statistics.
     * @param type type of statistics - max - ExecutionMaxTime, min - ExecutionMinTime,
     *      * avg - ExecutionAvgTime, count - ExecutionCount
     * @param sign sign of comparison
     * @param level comparison level
     * @return hibernate statistics.
     */
    Map<String, QueryStatistics> getQueryStatistics(String type, int sign, long level);
}
