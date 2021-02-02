package cselp.service.report;


import cselp.bean.FlightSearchCondition;
import cselp.domain.local.Person;
import cselp.dto.Flight;
import cselp.dto.FlightSearchResult;
import cselp.exception.RAException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;
import java.util.List;

public interface IReportComposer {
    /**
     * Returns PersonFlightReport for specified person as JSON string.
     * If report found in external cache, returns cached version, else creates new statistics and store into cache.
     * @param personId person identifier
     * @return PersonFlightReport for specified person as JSON string
     * @throws RAException if any
     * @throws JsonProcessingException if any
     */
    String getPersonFlightReport(Long personId) throws RAException, JsonProcessingException;

    /**
     * Deletes PersonFlightReport from external cache.
     * @param personId person identifier
     */
    void dropPersonFlightReport(Long personId);

    /**
     * Searches flights using specified conditions. DB view VW_FLIGHT_BY_PERSON used to select flights.
     * @param condition search conditions
     * @return FlightSearchResult dto object
     * @throws RAException if any
     */
    FlightSearchResult searchFlights(FlightSearchCondition condition) throws RAException;

    /**
     * Searches flights using specified conditions. Part of conditions used to select trips,
     * then legs filtered by severity conditions.
     * @param condition search conditions
     * @return FlightSearchResult dto object
     * @throws RAException if any
     */
    FlightSearchResult findFlights(FlightSearchCondition condition) throws RAException;

    /**
     * Returns flights withing specified date range.
     * @param from start date for flight search
     * @param to end date for flight search
     * @return list of flights
     * @throws RAException if any
     */
    List<Flight> getFlights(Date from, Date to) throws RAException;

    /**
     * Composes flight dto for specified leg.
     * @param legId leg identifier
     * @return Flight object
     * @throws RAException if any
     */
    Flight getFlight(Long legId) throws RAException;

    /**
     * Returns last flights for specified person.
     * 'Count' value limits returned list size.
     * @param personId person identifier
     * @param count required number of flights
     * @return list of flights
     * @throws RAException if any
     */
    List<Flight> getLastFlights(Long personId, int count) throws RAException;

    /**
     * Returns full person statistics - serialized JSON string of StatPilot object.
     * If such statistics found in external cache, returns cached version,
     * else creates new statistics and store into cache.
     * @param p Person object
     * @return statistics as JSON string
     * @throws RAException if any
     * @throws JsonProcessingException if any
     */
    String getFullPersonStatistics(Person p) throws RAException, JsonProcessingException;

    /**
     * Updates full person statistics in external cache for subsequent usage.
     * @param p Person object
     * @throws RAException if any
     * @throws JsonProcessingException if any
     */
    void updateFullPersonStatistics(Person p) throws RAException, JsonProcessingException;
}
