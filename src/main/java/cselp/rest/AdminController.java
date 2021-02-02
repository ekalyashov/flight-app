package cselp.rest;

import cselp.Const;
import cselp.bean.LdapUser;
import cselp.bean.PersonSearchCondition;
import cselp.domain.local.Division;
import cselp.domain.local.Person;
import cselp.exception.RAException;
import cselp.service.IDataSyncService;
import cselp.service.IFastStore;
import cselp.service.ILoadFlightsDataTask;
import cselp.service.external.IFlightService;
import cselp.service.ldap.ILdapService;
import cselp.service.local.ILFlightService;
import cselp.service.report.IReportComposer;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.stat.QueryStatistics;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * rest controller class for administrative purposes.
 * URL sample http://localhost:8888/cselp/rest/admin/...
 */
@RestController

@Api(value = "/", description = "Description of Admin API")
@RequestMapping("/admin")
public class AdminController {
    private static final Log log = LogFactory.getLog(AdminController.class);

    @Resource(name = "lFlightService")
    private ILFlightService lFlightService;

    @Resource(name = "flightService")
    private IFlightService flightService;

    @Resource(name = "dataSyncService")
    private IDataSyncService dataSyncService;

    @Resource(name = "ldapService")
    private ILdapService ldapService;

    @Resource(name = "appConfig")
    private Properties appConfig;

    @Resource(name = "fastStore")
    private IFastStore fastStore;

    @Resource(name = "reportComposer")
    private IReportComposer reportComposer;

    @Resource(name = "loadFlightsDataTask")
    private ILoadFlightsDataTask loadFlightsDataTask;

    @ModelAttribute
    public void setAppTsResponseHeader(HttpServletResponse response) {
        String appBuildTimestamp = appConfig.getProperty("app.build.timestamp", "0");
        response.setHeader(Const.APP_BUILD_TIMESTAMP, appBuildTimestamp);
    }

    /**
     * Returns list of users, searched in specified LDAP base.
     * @return string of semicolon-separated users CN
     */
    @ApiOperation(value = "Ldap users")
    @RequestMapping(value = "/ldap/users", method = RequestMethod.GET)
    public String getLdapUsers() {
        try {
            return ldapService.getUsers();
        }
        catch (Exception e) {
            log.error("Error in getLdapUsers.", e);
            return e.getLocalizedMessage();
        }
    }

    /**
     * Returns value of parameter flight.times.delta -
     * delta time in seconds to extend synchronization time range for departure and arrival
     * @return value of parameter flight.times.delta
     */
    @RequestMapping(value = "/flight/times/delta", method = RequestMethod.GET)
    public String getDeltaTime() {
        try {
            String deltaTime = appConfig.getProperty("flight.times.delta");
            return deltaTime;
        }
        catch (Exception ex) {
            log.error("getDeltaTime error.", ex);
            return "getDeltaTime error : " + ex;
        }
    }

    /**
     * Sets new value of parameter flight.times.delta
     * @param value new value of delta time
     * @return old and new values of delta time
     */
    @RequestMapping(value = "/flight/times/delta", method = RequestMethod.PUT)
    public String setDeltaTime(@RequestParam(value="value") String value) {
        try {
            String oldValue = String.valueOf(appConfig.setProperty("flight.times.delta", value));
            return "Old value " + oldValue + ", new value " + value;
        }
        catch (Exception ex) {
            log.error("setDeltaTime error.", ex);
            return "setDeltaTime error : " + ex;
        }
    }

    /**
     * Returns list of divisions in JSON format.
     * @return list of Division
     */
    @RequestMapping(value = "/divisions", method = RequestMethod.GET)
    public List<Division> findDivisions() {
        try {
            return lFlightService.findDivisions();
        }
        catch (Exception ex) {
            log.error("findDivisions error.", ex);
            return null;
        }
    }

    /**
     * Returns list of persons, belongs to specified squadron, in JSON format.
     * @param squadronId squadron identifier
     * @return list of Person
     */
    @ApiOperation(value = "persons")
    @RequestMapping(value = "/persons", method = RequestMethod.GET)
    public List<Person> findPersons(@RequestParam(value="squadronId") Long squadronId) {
        try {
            PersonSearchCondition psc = new PersonSearchCondition();
            psc.setSquadronId(squadronId);
            return lFlightService.findPersons(psc);
        }
        catch (Exception ex) {
            log.error("findPersons error.", ex);
            return null;
        }
    }

    /**
     * Gets xml encoded as x-www-form-urlencoded, parse as OpenSky flights format and store to database.
     * @param xml flights xml document
     * @param request HttpServletRequest
     * @return parse status
     */
    @RequestMapping(consumes = "application/x-www-form-urlencoded;charset=UTF-8",
            value = "/opensky/flights", method = RequestMethod.POST)
    public String parseFlights(@RequestParam(value="xml") String xml, HttpServletRequest request) {
        try {
            String from = "rest call";
            if (request != null) {
                from = request.getRequestURI();
            }
            return dataSyncService.parseFlights(xml, from);
        }
        catch (Exception e) {
            log.error("Error in parseFlights.", e);
            return e.getLocalizedMessage();
        }
    }

    /**
     * Initialize loading of OpenSky flights documents
     * @return status of operation
     */
    @RequestMapping(value = "/opensky/load", method = RequestMethod.GET)
    public String loadFlights() {
        try {
            loadFlightsDataTask.runLoadFlightsFiles();
            return "Success";
        }
        catch (Exception e) {
            log.error("Error in loadFlights.", e);
            return e.getLocalizedMessage();
        }
    }

    /**
     * Initialize synchronization of loaded OpenSky flights
     * @return status of operation
     */
    @RequestMapping(value = "/opensky/sync", method = RequestMethod.GET)
    public String syncFlights() {
        try {
            dataSyncService.syncFlightsAndLegs();
            return "Success";
        }
        catch (Exception e) {
            log.error("Error in syncFlights.", e);
            return e.getLocalizedMessage();
        }
    }

    /**
     * Try to login as LDAP user to predefined LDAP server.
     * @param login ldap user login
     * @param password ldap user password
     * @return status of operation
     */
    @RequestMapping(value = "/ldap/login", method = RequestMethod.GET)
    public String login(@RequestParam(value="login") String login,
                               @RequestParam(value="password") String password) {
        try {
            return  ldapService.login(login, password);
        }
        catch (Exception e) {
            log.error("Error in login.", e);
            return e.getLocalizedMessage();
        }
    }

    /**
     * Try to authorize specified user: searches user using specified LDAP attribute -
     * f.e. sAMAccountName or userPrincipalName,
     * then try to login this user with user's DN and specified password.
     * @param login user search attribute
     * @param password user password
     * @return status of operation
     */
    @RequestMapping(value = "/ldap/trylogin", method = RequestMethod.GET)
    public String tryLogin(@RequestParam(value="login") String login,
                        @RequestParam(value="password") String password) {
        try {
            LdapUser u = ldapService.tryLogin(login, password);
            return u == null ? "not found" : u.getDn();
        }
        catch (Exception e) {
            log.error("Error in login.", e);
            return e.getLocalizedMessage();
        }
    }

    /**
     * Returns Hibernate statistics for external database access - AirFASE.
     * type is comparison type. sign define comparison sign - > or <.
     * Values of specified type compares with level, according to sign, and added to result.
     * min, +1, 5 returns all statistics where ExecutionMinTime > 5 ms.
     * @param type type of statistics - max - ExecutionMaxTime, min - ExecutionMinTime,
     * avg - ExecutionAvgTime, count - ExecutionCount
     * @param sign sign of comparison,  {+1, -1}
     * @param level comparison level
     * @return hibernate statistics.
     */
    //@ApiOperation(value = "external statistics")
    @RequestMapping(value = "/external/statistics", method = RequestMethod.GET)
    public Map<String, QueryStatistics> getExternalStatistics(@RequestParam(value="type") String type,
                                                              @RequestParam(value="sign") Integer sign,
                                                              @RequestParam(value="level") Long level) {
        try {
            return flightService.getQueryStatistics(type, sign, level);
        }
        catch (Exception ex) {
            log.error("getExternalStatistics error.", ex);
            return null;
        }
    }

    /**
     * Returns hibernate statistics for local database calls - SmartFlight.
     * type is comparison type. sign define comparison sign - > or <.
     * Values of specified type compares with level, according to sign, and added to result.
     * min, +1, 5 returns all statistics where ExecutionMinTime > 5 ms.
     * @param type type of statistics - max - ExecutionMaxTime, min - ExecutionMinTime,
     * avg - ExecutionAvgTime, count - ExecutionCount
     * @param sign sign of comparison,  {+1, -1}
     * @param level comparison level
     * @return hibernate statistics.
     */
    @RequestMapping(value = "/local/statistics", method = RequestMethod.GET)
    public Map<String, QueryStatistics> getLocalStatistics(@RequestParam(value="type") String type,
                                                           @RequestParam(value="sign") Integer sign,
                                                           @RequestParam(value="level") Long level) {
        try {
            return lFlightService.getQueryStatistics(type, sign, level);
        }
        catch (Exception ex) {
            log.error("getLocalStatistics error.", ex);
            return null;
        }
    }

    /**
     * Returns person update info using specified person tabNo.
     * person update info stored in fastStore service (Redis) and can be inaccessible.
     * @param tabNo person tabNo
     * @return list of strings - differences of old and updated persons
     */
    @ApiOperation(value = "updated persons")
    @RequestMapping(value = "/person/update", method = RequestMethod.GET)
    public List<String> getPersonUpdates(@RequestParam(value="tabNo") String tabNo) {
        try {
            return fastStore.getPersonUpdate(tabNo);
        }
        catch (Exception ex) {
            log.error("getPersonUpdates error.", ex);
            return null;
        }
    }

    /**
     * Returns FlightReport for specified Person, in JSON format.
     * @param personId Person identifier
     * @return FlightReport for specified Person, in JSON format
     */
    @RequestMapping(value = "/report/person/flight", method = RequestMethod.GET)
    public ResponseEntity<String> getPersonFlightReport(@RequestParam(value="personId") Long personId)
            throws JsonProcessingException, RAException {
        try {
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
            String res = reportComposer.getPersonFlightReport(personId);
            return new ResponseEntity<>(res, httpHeaders, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in getPersonFlightReport.", e);
            throw e;
        }
    }

    /**
     * Deletes FlightReport for specified Person from fastStore service (Redis).
     * Next call of /report/person/flight for this Person will recalculate report and store it in fastStore service.
     * @param personId Person identifier
     * @return status of operation
     */
    @RequestMapping(value = "/report/person/flight/drop", method = RequestMethod.GET)
    public String dropPersonFlightReport(@RequestParam(value="personId") Long personId) {
        try {
            reportComposer.dropPersonFlightReport(personId);
            return "Success";
        }
        catch (Exception e) {
            log.error("Error in getPersonFlightReport.", e);
            return e.getLocalizedMessage();
        }
    }

    /**
     * Updates scores for uncompleted EventScore dto.
     * @param maxRecords maximum number of records to be updated
     * @return status string
     */
    @RequestMapping(value = "/score/update", method = RequestMethod.GET)
    public String updateEventScores(@RequestParam(value="maxRecords") Integer maxRecords) {
        try {
            int updated = dataSyncService.updateEventScores(maxRecords);
            return "Success, updated records " + updated;
        }
        catch (Exception e) {
            log.error("Error in getPersonFlightReport.", e);
            return "Update error: " + e.getLocalizedMessage();
        }
    }
}
