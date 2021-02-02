package cselp;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;

/**
 * Class to store common application constants.
 */
public class Const {

    /**
     * Name of session attribute where user data stored.
     */
    public static final String USER_SESSION_DATA = "USER_SESSION_DATA";

    public static final int LAST_FLIGHTS_COUNT = 5;
    public static final int TOP_STAT_COUNT = 5;
    /**
     * Json media type, value for "ContentType' http header.
     */
    public static final MediaType MEDIA_TYPE_APP_JSON =
            new MediaType("application", "json", Charset.forName("UTF-8"));

    public static final String APP_BUILD_TIMESTAMP = "OF.API.TS";
    /**
     * This interface contains constants of log classifier
     */
    public interface LogClassifier {
        String GENERAL                      = "GENERAL";
        String OS_FLIGHT_LINK               = "OS_FLIGHT_LINK";
        String OS_FLIGHT_LOAD               = "OS_FLIGHT_LOAD";
        String EVT_CALC                     = "EVT_CALC";
        String REPORT_UPDATE                = "REPORT_UPDATE";
    }

    /**
     * This interface contains codes, used for all log events
     */
    public interface LogCode {
        String UNKNOWN_ERROR                = "UNKNOWN_ERROR";
        String LINK_TIMEOUT1                = "LINK_TIMEOUT1";
        String LINK_TIMEOUT2                = "LINK_TIMEOUT2";
        String LINK_TIMEOUT3                = "LINK_TIMEOUT3";
        String FLIGHT_MMATCH                = "FLIGHT_MMATCH";
        String LINK_RESULT                  = "LINK_RESULT";
        String AIRPORT_DUP_ICAO_IATA        = "AIRPORT_DUP_ICAO_IATA";
        String AIRPORT_DUP_ICAO             = "AIRPORT_DUP_ICAO";
        String AIRPORT_DUP_IATA             = "AIRPORT_DUP_IATA";
        String AIRPORT_CONFLICT_ICAO_IATA   = "AIRPORT_CONFLICT_ICAO_IATA";
        String AIRPORT_CONFLICT_ICAO        = "AIRPORT_CONFLICT_ICAO";
        String AIRPORT_CONFLICT_IATA        = "AIRPORT_CONFLICT_IATA";
        String AIRPORT_NOCODES              = "AIRPORT_NOCODES";
        String LEG_INCOMPLETE               = "LEG_INCOMPLETE";
        String FLIGHT_DUP                   = "FLIGHT_DUP";
        String LOAD_RESULT                  = "LOAD_RESULT";
        String IO_ERROR                     = "IO_ERROR";
        String ARCHIVE_LOW_SPACE            = "ARCHIVE_LOW_SPACE";
        String ARCHIVE_NO_SPACE             = "ARCHIVE_NO_SPACE";
        String ARCHIVE_ERROR                = "ARCHIVE_ERROR";
        String CFTS_MISSING                 = "CFTS_MISSING";
        String AIRPORT_MISSING              = "AIRPORT_MISSING";
    }

    /**
     * Constants to define event severity.
     */
    public interface EventSeverity {
        short INFO      = 0;
        short LOW       = 1;
        short MEDIUM    = 2;
        short HIGH      = 3;
    }

    /**
     * Constants defines cache qualifiers for different object domains.
     */
    public interface Cache {
        String PERSONS          = "personsCache";
        String LOCAL            = "localCache";
        String EXTERNAL         = "externalCache";
        String LONG_EXTERNAL    = "longExternalCache";
    }

    /**
     * Defines application roles.
     */
    public interface AppRoles {
        long PILOT = 1;
        long SQ_LEADER = 2; //squadron leader
        long ADMIN = 3; //administrator
    }
}
