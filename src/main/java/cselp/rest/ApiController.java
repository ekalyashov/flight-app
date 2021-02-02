package cselp.rest;

import cselp.Const;
import cselp.bean.FlightSearchCondition;
import cselp.bean.LdapUser;
import cselp.domain.local.DivisionRole;
import cselp.domain.local.Person;
import cselp.domain.local.Squadron;
import cselp.domain.local.UserDto;
import cselp.dto.Flight;
import cselp.dto.FlightSearchResult;
import cselp.dto.Login;
import cselp.dto.User;
import cselp.exception.RAException;
import cselp.exception.RAUserException;
import cselp.service.external.IFlightService;
import cselp.service.ldap.ILdapService;
import cselp.service.local.ILFlightService;
import cselp.service.report.IReportComposer;
import cselp.util.DataUtil;
import cselp.util.ReportUtil;
import cselp.util.WebUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * rest controller class, implements API calls.
 * URL sample http://localhost:8888/cselp/rest/api/...
 */
@RestController
@Api(value = "/", description = "Description of Client API")
@RequestMapping("/api")
public class ApiController {
    private static final Log log = LogFactory.getLog(ApiController.class);
    private static final Log auditLog = LogFactory.getLog("audit." + ApiController.class.getName());

    private static final ObjectMapper mapper = new ObjectMapper();

    @Resource(name = "lFlightService")
    private ILFlightService lFlightService;

    @Resource(name = "flightService")
    private IFlightService flightService;

    @Resource(name = "ldapService")
    private ILdapService ldapService;

    @Resource(name = "reportComposer")
    private IReportComposer reportComposer;

    @Resource(name = "appConfig")
    private Properties appConfig;

    @ModelAttribute
    public void setAppTsResponseHeader(HttpServletResponse response) {
        String appBuildTimestamp = appConfig.getProperty("app.build.timestamp", "0");
        response.setHeader(Const.APP_BUILD_TIMESTAMP, appBuildTimestamp);
    }

    /**
     * Returns flight report, selects flights withing specified date range.
     * @param from start date for flight search
     * @param to end date for flight search
     * @return  FlightSearchResult object
     * @throws RAException if any
     */
    @ApiOperation(value = "Flight Report")
    @RequestMapping(value = "/report/flight", method = RequestMethod.GET)
    public ResponseEntity<FlightSearchResult> getFlightReport(@RequestParam(value="from") String from,
                                                              @RequestParam(value="to") String to)
            throws RAException {
        try {
            Date dFrom = DataUtil.toDate(from);
            Date dTo = DataUtil.toDate(to);
            if (dFrom == null || dTo == null) {
                throw new RAUserException("Invalid date format");
            }
            List<Flight> flights = reportComposer.getFlights(dFrom, dTo);
            return new ResponseEntity<>(new FlightSearchResult(flights.size(), flights), HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in getFlightReport.", e);
            throw e;
        }
    }

    /**
     * Returns flight denoted by flightId parameter.
     * @param flightId flight identifier
     * @return Flight object
     * @throws Exception if any
     */
    @RequestMapping(value = "/flight/{flightId}", method = RequestMethod.GET)
    public ResponseEntity<Flight> getFlight(@PathVariable(value = "flightId") Long flightId)
            throws Exception {
        boolean applyMock = Boolean.parseBoolean(appConfig.getProperty("mock.apply", "true"));
        if (applyMock) {
            return mockGetFlight(flightId);
        }
        try {
            Flight flight = reportComposer.getFlight(flightId);
            return new ResponseEntity<>(flight, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in getFlightReport.", e);
            throw e;
        }
    }

    /**
     * Mock implementation of 'getFlight' method
     * @param flightId flight identifier
     * @return Flight object
     * @throws Exception if any
     */
    public ResponseEntity<Flight> mockGetFlight(@PathVariable(value = "flightId") Long flightId)
            throws Exception {
        try {
            URL url = this.getClass().getClassLoader().getResource("flight.json");
            File file = new File(url.toURI());
            String json = FileUtils.readFileToString(file, "UTF-8");
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
            Flight res = mapper.readValue(json, Flight.class);
            return new ResponseEntity<>(res, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in mockFindFlights.", e);
            throw e;
        }
    }

    /**
     * Returns flight report, selects flights using specified conditions.
     * DB view used to select flights, call can be slow.
     * @param from start date for flight search
     * @param to end date for flight search
     * @param arrival arrival airport ICAO code
     * @param departure departure airport ICAO code
     * @param high select high severity
     * @param med select medium severity
     * @param low select low severity
     * @param first offset for first result
     * @param amount max results count
     * @param order 'asc' or 'desc' flight's departure date order
     * @param utc mean UTC time
     * @return FlightSearchResult object
     * @throws Exception if any
     */
    @RequestMapping(value = "/flights", method = RequestMethod.GET)
    public ResponseEntity<FlightSearchResult> findFlights(@RequestParam(value="dep_from", required = false) String from,
                                                          @RequestParam(value="dep_to", required = false) String to,
                                                          @RequestParam(value="arr_a", required = false) String arrival,
                                                          @RequestParam(value="dep_a", required = false) String departure,
                                                          //@RequestParam(value="role", required = false) String role,
                                                          @RequestParam(value="high", required = false) Boolean high,
                                                          @RequestParam(value="med", required = false) Boolean med,
                                                          @RequestParam(value="low", required = false) Boolean low,
                                                          @RequestParam(value="first", required = false) Integer first,
                                                          @RequestParam(value="amount", required = false) Integer amount,
                                                          @RequestParam(value = "order", required = false) String order,
                                                          @RequestParam(value="utc", required = false) Boolean utc)
            throws Exception {
        boolean applyMock = Boolean.parseBoolean(appConfig.getProperty("mock.apply", "true"));
        if (applyMock) {
            return mockFindFlights(from, to, arrival, departure, high, med, low, first, amount, order);
        }
        try {
            Person p = WebUtil.getCurrentPerson();
            if (p == null) {
                log.warn("User should be logged");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            FlightSearchCondition condition = new FlightSearchCondition();
            condition.getPersonIds().add(p.getId());
            condition.setFirstResult(first);
            condition.setMaxResults(amount);
            if (from != null) {
                condition.setFrom(DataUtil.toTimestamp(from));
            }
            if (to != null) {
                condition.setTo(DataUtil.toTimestamp(to));
            }
            condition.setOriginIcao(departure);
            condition.setDestinationIcao(arrival);
            condition.setOrder(order == null ? Boolean.FALSE : "asc".equalsIgnoreCase(order));
            //condition.setFlightRole(role);
            if (high != null || med != null || low != null) {
                List<Short> severity = new ArrayList<>();
                if (Boolean.TRUE.equals(high)) {
                    severity.add(Const.EventSeverity.HIGH);
                }
                if (Boolean.TRUE.equals(med)) {
                    severity.add(Const.EventSeverity.MEDIUM);
                }
                if (Boolean.TRUE.equals(low)) {
                    severity.add(Const.EventSeverity.LOW);
                }
                condition.setSeverity(severity);
            }
            condition.setUtc(Boolean.TRUE.equals(utc));
            FlightSearchResult res = reportComposer.searchFlights(condition);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in findFlights.", e);
            throw e;
        }
    }

    /**
     * Returns flight report, selects flights using specified conditions.
     * @param from start date for flight search
     * @param to end date for flight search
     * @param arrival arrival airport ICAO code
     * @param departure departure airport ICAO code
     * @param high select high severity
     * @param med select medium severity
     * @param low select low severity
     * @param first offset for first result
     * @param amount max results count
     * @param order 'asc' or 'desc' flight's departure date order
     * @return FlightSearchResult object
     * @throws Exception if any
     */
    @ApiOperation(value = "Flight Report")
    @RequestMapping(value = "/report/flights", method = RequestMethod.GET)
    public ResponseEntity<FlightSearchResult> findReportFlights(@RequestParam(value="dep_from", required = false) String from,
                                    @RequestParam(value="dep_to", required = false) String to,
                                    @RequestParam(value="arr_a", required = false) String arrival,
                                    @RequestParam(value="dep_a", required = false) String departure,
                                    //@RequestParam(value="role", required = false) String role,
                                    @RequestParam(value="high", required = false) Boolean high,
                                    @RequestParam(value="med", required = false) Boolean med,
                                    @RequestParam(value="low", required = false) Boolean low,
                                    @RequestParam(value="first", required = false) Integer first,
                                    @RequestParam(value="amount", required = false) Integer amount,
                                    @RequestParam(value = "order", required = false) String order)
            throws Exception {
        boolean applyMock = Boolean.parseBoolean(appConfig.getProperty("mock.apply", "true"));
        if (applyMock) {
            return mockFindFlights(from, to, arrival, departure, high, med, low, first, amount, order);
        }
        try {
            Person p = WebUtil.getCurrentPerson();
            if (p == null) {
                log.warn("User should be logged");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            FlightSearchCondition condition = new FlightSearchCondition();
            condition.getPersonIds().add(p.getId());
            condition.setFirstResult(first);
            condition.setMaxResults(amount);
            if (from != null) {
                condition.setFrom(DataUtil.toTimestamp(from));
            }
            if (to != null) {
                condition.setTo(DataUtil.toTimestamp(to));
            }
            condition.setOriginIcao(departure);
            condition.setDestinationIcao(arrival);
            condition.setOrder(order == null ? Boolean.FALSE : "asc".equalsIgnoreCase(order));
            //condition.setFlightRole(role);
            if (high != null || med != null || low != null) {
                List<Short> severity = new ArrayList<>();
                if (Boolean.TRUE.equals(high)) {
                    severity.add(Const.EventSeverity.HIGH);
                }
                if (Boolean.TRUE.equals(med)) {
                    severity.add(Const.EventSeverity.MEDIUM);
                }
                if (Boolean.TRUE.equals(low)) {
                    severity.add(Const.EventSeverity.LOW);
                }
                condition.setSeverity(severity);
            }
            FlightSearchResult res = reportComposer.findFlights(condition);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in findReportFlights.", e);
            throw e;
        }
    }

    /**
     * Mock implementation of 'findFlights' method
     * @param from start date for flight search
     * @param to end date for flight search
     * @param arrival arrival airport ICAO code
     * @param departure departure airport ICAO code
     * @param high select high severity
     * @param med select medium severity
     * @param low select low severity
     * @param first offset for first result
     * @param amount max results count
     * @param order 'asc' or 'desc' flight's departure date order
     * @return FlightSearchResult object
     * @throws Exception if any
     */
    @RequestMapping(value = "/mock/flights", method = RequestMethod.GET)
    public ResponseEntity<FlightSearchResult> mockFindFlights(@RequestParam(value = "dep_from", required = false) String from,
                                                  @RequestParam(value = "dep_to", required = false) String to,
                                                  @RequestParam(value = "arr_a", required = false) String arrival,
                                                  @RequestParam(value = "dep_a", required = false) String departure,
                                                  //@RequestParam(value="role", required = false) String role,
                                                  @RequestParam(value = "high", required = false) Boolean high,
                                                  @RequestParam(value = "med", required = false) Boolean med,
                                                  @RequestParam(value = "low", required = false) Boolean low,
                                                  @RequestParam(value = "first", required = false) Integer first,
                                                  @RequestParam(value = "amount", required = false) Integer amount,
                                                  @RequestParam(value = "order", required = false) String order)
            throws Exception {
        try {
            URL url = this.getClass().getClassLoader().getResource("flights.json");
            File file = new File(url.toURI());
            String json = FileUtils.readFileToString(file, "UTF-8");
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
            FlightSearchResult res = mapper.readValue(json, FlightSearchResult.class);
            return new ResponseEntity<>(res, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in mockFindFlights.", e);
            throw e;
        }
    }

    /**
     * Logging user on after successful authentication.
     * @param input Login object
     * @return User object
     * @throws Exception if any
     */
    @RequestMapping(consumes = "application/json",
            value = "/login", method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody Login input) throws Exception {
        boolean applyMock = Boolean.parseBoolean(appConfig.getProperty("mock.apply", "true"));
        boolean demoLogin = Boolean.parseBoolean(appConfig.getProperty("demo.login", "false"));
        String tabNumString = appConfig.getProperty("demo.tab.numbers", "");
        List<String> tabNums = Arrays.asList(tabNumString.split(","));
        String demoPassword = appConfig.getProperty("demo.password", "");
        String loginDB = appConfig.getProperty("demo.password", "LDAP");
        if (applyMock) {
            return mockLogin(input);
        }
        try {
            if (input == null || input.getTab_num() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Person p = lFlightService.findPerson(input.getTab_num());
            if (p != null) {
                if (Boolean.FALSE.equals(input.getStrict())) {
                    auditLog.warn("Non-strict login for '" + input.getTab_num() + "'");
                    User res = assignCurrentUser(input, p);
                    return new ResponseEntity<>(res, HttpStatus.OK);
                }
                else {
                    if (demoLogin) {
                        //if (tabNums.contains(input.getTab_num())) {
                            boolean success = demoPassword.equals(input.getPassword());
                            auditLog.warn("Demo login for '" + input.getTab_num() + "' " + (success ? "success" : "failed"));
                            if (success) {
                                User res = assignCurrentUser(input, p);
                                return new ResponseEntity<>(res, HttpStatus.OK);
                            }
                        //}
                    }
                    if ("LDAP".equals(loginDB)) {
                        //LDAP login
                        LdapUser ldapUser = ldapService.tryLogin(input.getTab_num(), input.getPassword());
                        if (ldapUser == null) {
                            auditLog.warn("Ldap login for '" + input.getTab_num() + "' : invalid credentials");
                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                        } else {
                            Map<Long, Squadron> squadronMap = lFlightService.findSquadronMap();
                            Map<Long, DivisionRole> divisionRolesMap = lFlightService.getDivisionRolesMap();
                            User res = ReportUtil.toUser(p, squadronMap, divisionRolesMap);
                            WebUtil.setCurrentUser(ldapUser, p, res);
                            auditLog.info("Ldap login for '" + input.getTab_num() + "' : success");
                            return new ResponseEntity<>(res, HttpStatus.OK);
                        }
                    }
                    else {
                        //local DB login
                        UserDto dbUser = lFlightService.tryLogin(p.getId(), input.getPassword());
                        if (dbUser == null) {
                            auditLog.warn("DB login for '" + input.getTab_num() + "' : invalid credentials");
                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                        }
                        else {
                            User res = assignCurrentUser(input, p);
                            return new ResponseEntity<>(res, HttpStatus.OK);
                        }
                    }
                }
            }
            else {
                auditLog.error("invalid principal for user '" + input.getTab_num() + "'");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e) {
            log.error("Error in login.", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User assignCurrentUser(@RequestBody Login input, Person p) throws RAException {
        Map<Long, Squadron> squadronMap = lFlightService.findSquadronMap();
        Map<Long, DivisionRole> divisionRolesMap = lFlightService.getDivisionRolesMap();
        User res = ReportUtil.toUser(p, squadronMap, divisionRolesMap);
        LdapUser ldapUser = new LdapUser();
        ldapUser.setLogonName(input.getTab_num());
        WebUtil.setCurrentUser(ldapUser, p, res);
        return res;
    }

    /**
     * Makes log out of current user.
     * @return HttpStatus OK
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout() {
        User u = WebUtil.getCurrentUser();
        if (u != null) {
            auditLog.info("User '" + u.getTab_num() + "' logged out");
        }
        WebUtil.setCurrentUser(null, null, null);
        WebUtil.closeSession();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Mock implementation of 'login' method
     * @param input Login object
     * @return User object
     * @throws Exception if any
     */
    @RequestMapping(consumes = "application/json",
            value = "/mock/login", method = RequestMethod.POST)
    public ResponseEntity<User> mockLogin(@RequestBody Login input) throws Exception {
        try {
            URL url = this.getClass().getClassLoader().getResource("login.json");
            File file = new File(url.toURI());
            String json = FileUtils.readFileToString(file, "UTF-8");
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
            User user = mapper.readValue(json, User.class);
            return new ResponseEntity<>(user, httpHeaders, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in mockLogin.", e);
            throw e;
        }
    }

    /**
     * Mock implementation of 'login' method, when caller request GET method
     * @param response HttpServletResponse
     * @return predefined user info
     * @throws Exception if any
     */
    @RequestMapping(value = "/loginget", method = RequestMethod.GET)
    public String mockLoginGet(HttpServletResponse response) throws Exception {
        try {
            URL url = this.getClass().getClassLoader().getResource("login.json");
            File file = new File(url.toURI());
            String json = FileUtils.readFileToString(file, "UTF-8");
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
            response.addHeader("Content-Type", "application/json;charset=UTF-8");
            return json;
        }
        catch (Exception e) {
            log.error("Error in mockLoginGet.", e);
            throw e;
        }
    }

    /**
     * Returns User object for current user.
     * @return User object
     * @throws Exception if any
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<User> getCurrentUser() throws Exception {
        boolean applyMock = Boolean.parseBoolean(appConfig.getProperty("mock.apply", "true"));
        if (applyMock) {
            return mockGetCurrentUser();
        }
        User res = WebUtil.getCurrentUser();
        if (res == null) {
            log.warn("User should be logged");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    /**
     * Mock implementation of 'getCurrentUser' method
     * @return predefined user info
     * @throws Exception if any
     */
    @RequestMapping(value = "/mock/user", method = RequestMethod.GET)
    public ResponseEntity<User> mockGetCurrentUser() throws Exception {
        try {
            URL url = this.getClass().getClassLoader().getResource("user.json");
            File file = new File(url.toURI());
            String json = FileUtils.readFileToString(file, "UTF-8");
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
            User user = mapper.readValue(json, User.class);
            return new ResponseEntity<>(user, httpHeaders, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in login.", e);
            throw e;
        }
    }

    /**
     * Returns short version of statistics for specified pilot.
     * @param tabNum pilot registration number
     * @return short report - serialized JSON string of StatPilot object
     * @throws Exception if any
     */
    @RequestMapping(value = "/pilot/{tab_num}/stat/short", method = RequestMethod.GET)
    public ResponseEntity<String> shortPersonStatistics(@PathVariable(value = "tab_num") String tabNum)
            throws Exception {
        boolean applyMock = Boolean.parseBoolean(appConfig.getProperty("mock.apply", "true"));
        if (applyMock) {
            return  mockShortPersonStatistics(tabNum);
        }
        return fullPersonStatistics(tabNum);
    }

    /**
     * Mock implementation of 'shortPersonStatistics' method
     * @param tabNum pilot registration number
     * @return predefined pseudo-statistics data
     * @throws Exception if any
     */
    @RequestMapping(value = "/mock/pilot/{tab_num}/stat/short", method = RequestMethod.GET)
    public ResponseEntity<String> mockShortPersonStatistics(@PathVariable(value = "tab_num") String tabNum)
            throws Exception {
        try {
            URL url = this.getClass().getClassLoader().getResource("pilot_stat.json");
            File file = new File(url.toURI());
            String json = FileUtils.readFileToString(file, "UTF-8");
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
            return new ResponseEntity<>(json, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in getPersonFlightReport.", e);
            throw e;
        }
    }

    /**
     * Returns short version of statistics for specified pilot.
     * @param tabNum pilot registration number
     * @return report string - serialized JSON string of StatPilot object
     * @throws Exception if any
     */
    @RequestMapping(value = "/pilot/{tab_num}/stat/full", method = RequestMethod.GET)
    public ResponseEntity<String> fullPersonStatistics(@PathVariable(value = "tab_num") String tabNum)
            throws Exception {
        try {
            Person p = lFlightService.findPerson(tabNum);
            if (p != null) {
                HttpHeaders httpHeaders= new HttpHeaders();
                httpHeaders.setContentType(Const.MEDIA_TYPE_APP_JSON);
                String res = reportComposer.getFullPersonStatistics(p);
                return new ResponseEntity<>(res, httpHeaders, HttpStatus.OK);
            }
            else {
                log.warn("unknown person tab num: " + tabNum);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e) {
            log.error("Error in login.", e);
            throw e;
        }
    }

    /**
     * Updates statistics for specified pilot. Used for debug purposes.
     * @param tabNum pilot registration number
     * @return status string
     * @throws Exception if any
     */
    @RequestMapping(value = "/pilot/{tab_num}/stat/full/update", method = RequestMethod.GET)
    public ResponseEntity<String> updateFullPersonStatistics(@PathVariable(value = "tab_num") String tabNum)
            throws Exception {
        try {
            Person p = lFlightService.findPerson(tabNum);
            if (p != null) {
                reportComposer.updateFullPersonStatistics(p);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
            else {
                log.warn("unknown person tab num: " + tabNum);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e) {
            log.error("Error in login.", e);
            throw e;
        }
    }

}
