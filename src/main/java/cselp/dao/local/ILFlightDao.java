package cselp.dao.local;


import cselp.bean.PersonSearchCondition;
import cselp.dao.IGenericDao;
import cselp.domain.local.*;
import cselp.exception.RADbException;
import cselp.exception.RAException;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Timestamp;
import java.util.*;

public interface ILFlightDao extends IGenericDao {

    /**
     * Persists specified log message
     * @param entity LogMessage object
     * @return persisted entity
     * @throws RAException if any
     */
    LogMessage createLogMessage(LogMessage entity) throws RAException;

    /**
     * Searches division by specified name. Returns Division only, without squadrons
     * @param name division name
     * @return Division or null, if name is invalid
     * @throws RAException if any
     */
    Division getDivision(String name) throws RAException;

    /**
     * Searches division by specified name. Returns Division with squadrons
     * @param name division name
     * @return Division or null, if name is invalid
     * @throws RAException if any
     */
    Division getDivisionFull(String name) throws RAException;

    /**
     * Creates new division with specified name
     * @param name division name
     * @return new division entity
     * @throws RAException  if any
     */
    Division createDivision(String name) throws RAException;

    /**
     * Searches squadron by specified division identifier and name.
     * @param divisionId division identifier
     * @param name squadron name
     * @return Squadron or null if squadron wasn't found
     * @throws RAException if any
     */
    Squadron getSquadron(Long divisionId, String name) throws RAException;

    /**
     * Creates new squadron with specified name, linked to supplied division
     * @param divisionId division identifier
     * @param name squadron name
     * @return new Squadron entity
     * @throws RAException if any
     */
    Squadron createSquadron(Long divisionId, String name) throws RAException;

    /**
     * Returns map divisionRoleId -> DivisionRole object
     * @return reference map of division roles
     * @throws RAException if any
     */
    Map<Long, DivisionRole> getDivisionRolesMap() throws RAException;

    /**
     * Persists specified DivisionRole
     * @param entity DivisionRole entity
     * @return persisted DivisionRole object
     * @throws RAException if any
     */
    DivisionRole saveDivisionRole(DivisionRole entity) throws RAException;

    /**
     * Returns map personId -> Person entity, using specified set of person identifiers.
     * @param personIds of person identifiers
     * @return map personId -> Person entity
     * @throws RAException if any
     */
    Map<Long, Person> getPersonMap(Collection<Long> personIds) throws RAException;

    /**
     * Searches Person by specified personnel number.
     * @param tabNum personnel number
     * @return Person entity or null
     * @throws RAException if any
     */
    Person findPerson(String tabNum) throws RAException;

    /**
     * Creates new person
     * @param entity new Person data
     * @return new person
     * @throws RAException if any
     */
    Person createPerson(Person entity) throws RAException;

    /**
     * Updates person entity
     * @param entity Person data to update
     * @return updated person
     * @throws RAException if any
     */
    Person updatePerson(Person entity) throws RAException;

    /**
     *Searches trips according to specified flight parameters.
     * @param carrier     carrier
     * @param flightNum   flight number
     * @param flightDate  flight scheduled date/time
     * @param actualDate  flight actual date/time
     * @param tailNum     airplane registration number
     * @param origin      origin airport code
     * @param destination destination airport code
     * @param flightKind  kind of flight
     * @return list of trips
     * @throws RAException if any
     */
    List<Trip> findTrips(String carrier, String flightNum, Timestamp flightDate, Timestamp actualDate,
                   String tailNum, String origin, String destination, String flightKind)
            throws RAException;

    /**
     * Creates new Trip entity
     * @param entity trip data
     * @return new Trip entity
     * @throws RAException if any
     */
    Trip createTrip(Trip entity) throws RAException;

    /**
     * Updates leg entity.
     * @param entity leg data to update
     * @return updates leg entity
     * @throws RADbException if any
     */
    Leg updateLeg(Leg entity) throws RADbException;

    /**
     * Updates leg, adds link to flight.
     * @param entity leg entity
     * @return updated leg
     * @throws RADbException if any
     */
    Leg linkLeg(Leg entity) throws RADbException;

    /**
     * Returns leg with specified identifier.
     * @param id leg identifier
     * @return leg or null if not exists
     * @throws RADbException if any
     */
    Leg getLeg(Long id) throws RADbException;

    /**
     * Searches legs that hasn't link to flight, with departure date > specified due date
     * @param dueDate due date
     * @return list of legs
     * @throws RAException if any
     */
    List<Leg> findUnlinkedLegs(Timestamp dueDate) throws RAException;

    /**
     * Returns number of legs that hasn't link to flight, with departure date < specified up date.
     * @param upDate up date
     * @return number of legs meeting to requirements
     * @throws RAException if any
     */
    long countOldUnlinkedLegs(Timestamp upDate) throws RAException;

    /**
     * Searches legs that hasn't link to flight, with departure date withing specified range
     * @param from start date range
     * @param to end date range
     * @return list of legs
     * @throws RAException if any
     */
    List<Leg> findUnlinkedLegs(Date from, Date to) throws RAException;

    /**
     * Searches persons belongs to legs, denoted by specified identifiers.
     * @param legIds collection of leg identifiers
     * @return list of persons
     * @throws RAException if any
     */
    List<Person> findPersonsForLegs(Collection<Long> legIds) throws RAException;

    /**
     * Searches persons using supplied conditions
     * @param condition search conditions
     * @return list of persons
     * @throws RAException if any
     */
    List<Person> findPersons(PersonSearchCondition condition) throws RAException;

    /**
     * Returns all divisions from persistent storage.
     * @return list of divisions
     * @throws RAException  if any
     */
    List<Division> findDivisions() throws RAException;

    /**
     * Returns map of aircraft registration numbers
     * @return map: main reg number -> list of alternative reg numbers
     * @throws RADbException if any
     */
    Map<String, List<String>> getAircraftRegNumMap() throws RADbException;

    /**
     * Returns map of EventTypeScore entities. Map key is pair of event type and severity
     * @return map of EventTypeScore entities
     * @throws RADbException if any
     */
    Map<Pair<Long, Short>, EventTypeScore> getEventTypeScoresMap() throws RADbException;

    /**
     * Searches AirportCode objects with IATA and ICAO codes, equals to specified.
     * @param value AirportCode ofject with required IATA and ICAO codes
     * @return list of AirportCode objects
     * @throws RADbException if any
     */
    List<AirportCode> findAirportCodeByAllCodes(AirportCode value) throws RADbException;

    /**
     * Returns map airport codes -> AirportCode object. See AirportCode.getCodes() method.
     * @return reference map of AirportCode objects
     * @throws RAException if any
     */
    Map<String, AirportCode> getAirportCodesMap()  throws RADbException;

    /**
     * Persists new AirportCode entity
     * @param entity AirportCode data
     * @return AirportCode entity
     * @throws RAException if any
     */
    AirportCode createAirportCode(AirportCode entity) throws RADbException;

    /**
     * Updates existent AirportCode object.
     * @param entity AirportCode data to update
     * @return AirportCode entity
     * @throws RAException if any
     */
    AirportCode updateAirportCode(AirportCode entity) throws RADbException;

    /**
     * Persists new LoadedFlight object.
     * @param entity LoadedFlight data to update
     * @return LoadedFlight entity
     * @throws RAException if any
     */
    LoadedFlight createLoadedFlight(LoadedFlight entity) throws RADbException;

    /**
     * Searches LoadedFlight objects by specified file name.
     * @param fileName file name
     * @return list of LoadedFlight objects
     * @throws RAException if any
     */
    List<LoadedFlight> findLoadedFlights(String fileName) throws RADbException;

    /**
     * Searches incomplete event scores. EventScore's id should be > specified firstId.
     * 'maxResults' value limits returned list size to specified value.
     * @param maxResults returned list size limit
     * @param firstId minimal identifier to find event scores
     * @return list of EventScore objects
     * @throws RAException if any
     */
    List<EventScore> findIncompleteEventScores(int maxResults, long firstId) throws RADbException;

    /**
     * Updates existent EventScore objects
     * @return updated EventScore entity
     * @throws RAException if any
     */
    EventScore updateEventScore(EventScore entity) throws RADbException;

    /**
     * Calculates count of persons and sum of legs executed by persons, per month.
     * Persons belongs to specified squadron.
     * @param squadronId squadron identifier
     * @return list of PersonsSumByMonth objects, every object contains data for one month
     * @throws RAException if any
     */
    List<PersonsSumByMonth> findSquadronPersonSumLegs(Long squadronId) throws RAException;

    /**
     * Searches route statistics for specified person. If 'count' value defined,
     * limits returned resultset size to specified value.
     * @param personId person identifier
     * @param from start date from where statistics calculated
     * @param count returned list size limit
     * @return list of TopStat objects
     * @throws RAException if any
     */
    List<TopStat> findTopRoutesStat(Long personId, Date from, Integer count) throws RAException;

    /**
     * Searches statistics of aircraft usage for specified person. If 'count' value defined,
     * limits returned resultset size to specified value.
     * @param personId person identifier
     * @param from start flight departure date from where statistics calculated
     * @param count returned list size limit
     * @return list of TopStat objects
     * @throws RAException if any
     */
    List<TopStat> findTopPlanesStat(Long personId, Date from, Integer count) throws RAException;

    /**
     * Searches statistics of crew assignment for specified person. If 'count' value defined,
     * limits returned resultset size to specified value.
     * @param personId person identifier
     * @param from start flight departure date from where statistics calculated
     * @param count returned list size limit
     * @return list of TopStat objects
     * @throws RAException if any
     */
    List<TopStat> findPilotTopCrewStat(Long personId, Date from, Integer count) throws RAException;

    /**
     * Returns user dto object denoted by person identifier.
     * @param personId person identifier
     * @return user dto object
     * @throws RAException if any
     */
    UserDto getUserByPerson(Long personId) throws RAException;
}
