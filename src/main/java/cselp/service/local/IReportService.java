package cselp.service.local;


import cselp.bean.FlightSearchCondition;
import cselp.domain.local.EventScore;
import cselp.domain.local.Leg;
import cselp.domain.local.Trip;
import cselp.domain.report.PersonFlight;
import cselp.domain.report.PersonLeg;
import cselp.exception.RADbException;
import cselp.exception.RAException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IReportService {
    /**
     * Searches person flight by specified person identifier.
     * @param personId person identifier
     * @return list of PersonFlight objects
     * @throws RAException if any
     */
    List<PersonFlight> findPersonFlights(Long personId) throws RAException;

    /**
     * Returns map flightId -> list of EventScore objects
     * @param flightIds collection of flight identifiers
     * @return map of EventScore objects
     * @throws RAException if any
     */
    Map<Long, List<EventScore>> findEventScoresMap(Collection<Long> flightIds) throws RAException;

    /**
     * Searches PersonLeg set according to specified conditions
     * @param condition search conditions
     * @return tuple (full result size; list of PersonLeg objects)
     * @throws RAException if any
     */
    Pair<Long, List<PersonLeg>> findPersonLegsOpt(FlightSearchCondition condition) throws RAException;

    /**
     * Searches Trips set according to specified conditions
     * @param condition search conditions
     * @return tuple (full result size; list of Trip objects)
     * @throws RAException if any
     */
    Pair<Long, List<Trip>> findTrips(FlightSearchCondition condition) throws RAException;

    /**
     * Searches Trips where flight dates placed in specified range.
     * @param from start flight date
     * @param to end flight date
     * @return list of Trip objects
     * @throws RAException if any
     */
    List<Trip> findTrips(Date from, Date to) throws RAException;

    /**
     * Returns leg with specified identifier.
     * @param id leg id
     * @return Leg object
     * @throws RADbException if any
     */
    Leg getLeg(Long id) throws RADbException;

    /**
     * Searches trips and returns map trip id -> Trip entity
     * @param tripIds collection of trip identifiers
     * @return map trip id -> Trip entity
     * @throws RAException if any
     */
    Map<Long, Trip> getTripMap(Collection<Long> tripIds) throws RAException;

    /**
     * Searches legs by specified person id. If 'count' value defined,
     * limits returned resultset size to specified value.
     * @param personId person identifier
     * @param count returned list size limit
     * @return list of legs, sorted by departure time in descending order
     * @throws RAException if any
     */
    List<Leg> findLastLegs(Long personId, Integer count) throws RAException;

    /**
     * Returns all legs where specified person participated.
     * @param personId person identifier
     * @return list of legs
     * @throws RAException if any
     */
    List<Leg> findPersonLegs(Long personId) throws RAException;

    /**
     * Returns list of event scores for specified person.
     * @param personId person identifier
     * @return list of EventScore objects
     * @throws RADbException if any
     */
    List<EventScore> findPersonEventScores(Long personId) throws RADbException;
}
