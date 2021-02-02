package cselp.service;


import cselp.bean.ErrorMessage;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

/**
 * Interface of fast store service, using Redis server.
 * Fast store service used as cache for reports, to ensure reasonable access time to reports.
 */
public interface IFastStore {
    /**
     * Initialization method.
     */
    void init();

    /**
     * Adds person info updates to existed entry.
     * @param tabNo person identifier
     * @param jsonDiff updates of user info
     * @return the length of the list of changes
     */
    Long addPersonUpdate(String tabNo, JSONObject jsonDiff);

    /**
     * Returns list of info updates for specified person.
     * @param tabNo person identifier
     * @return list of info updates
     */
    List<String> getPersonUpdate(String tabNo);

    /**
     * Fast store methods don't returns exception if error occurred.
     * Application should check last error if such logic required.
     * @return last ErrorMessage or null;
     */
    ErrorMessage getLastError();

    /**
     * Returns flight report for specified person from redis.
     * @param personId person identifier
     * @return flight report for specified person, string
     */
    String findPersonFlightReport(Long personId);

    /**
     * Stores flight report for specified person into redis.
     * @param personId person identifier
     * @param value flight report, string
     */
    void storePersonFlightReport(Long personId, String value);

    /**
     * Drops flight report of specified person.
     * @param personId person identifier
     */
    void dropPersonFlightReport(Long personId);

    /**
     * Returns full statistics for specified person from redis.
     * @param personId person identifier
     * @return full statistics for specified person, string
     */
    String findFullPersonStatistics(Long personId);

    /**
     * Stores statistics for specified person into redis.
     * @param personId person identifier
     * @param value full statistics, string
     */
    void storeFullPersonStatistics(Long personId, String value);
}
