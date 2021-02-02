package cselp.service;


import cselp.bean.ErrorMessage;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Properties;

/**
 * Implementation of fast store service, using Redis server.
 * Fast store service used as cache for reports, to ensure reasonable access time to reports.
 */
public class RedisStore implements IFastStore {
    private static final Log log = LogFactory.getLog(RedisStore.class);
    //domain prefix for persons info updates
    public static final String PERSON_UPDATE_KEY_PREFIX = "person:update:";
    //domain prefix for persons flight reports
    public static final String PERSON_FLIGHTS_KEY_PREFIX = "person:flights:";
    //domain prefix for persons statistics
    public static final String PERSON_FULL_STATISTICS_KEY_PREFIX = "person:stat:full";
    private static final long TRY_CONNECT_TIMEOUT = 1000 * 60; //1 minute

    //last error message, to check if error occurred at last call
    private ThreadLocal<ErrorMessage> lm = new ThreadLocal<>();
    private JedisPool jedisPool = null;
    private Configuration appConfig;
    private static long connErrorTime;

    /**
     * setter for application properties
     * @param appProperties Properties bean
     */
    public void setAppProperties(Properties appProperties) {
        appConfig = ConfigurationConverter.getConfiguration(appProperties);
    }

    /**
     * Initialization method, creates jedisPool - pool of connects to Redis.
     */
    @Override
    public void init() {
        try {
            if (jedisPool != null) {
                jedisPool.destroy();
            }
            String redisHost = appConfig.getString("fast.store.redis.host", Protocol.DEFAULT_HOST);
            int redisPort = appConfig.getInt("fast.store.redis.port", Protocol.DEFAULT_PORT);
            jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);
        }
        catch (Exception e) {
            jedisPool = null;
            log.error("Initialization error. ", e);
            lm.set(new ErrorMessage("Initialization error.", e));
        }
    }

    public void destroy() {
        if (jedisPool != null) {
            jedisPool.destroy();
            jedisPool = null;
        }
    }

    /**
     * Checks timeout from last connection error.
     * Uses to exclude response delay if redis server inaccessible.
     * Trying to connect to inaccessible server takes a lot of time.
     * If last connection error occurred recently, cache methods should returns null immediately.
     * @return true if last connection error occurred > than TRY_CONNECT_TIMEOUT time ago, false otherwise.
     */
    private boolean connectTimeout() {
        return (System.currentTimeMillis() - connErrorTime) > TRY_CONNECT_TIMEOUT;
    }

    /**
     * Adds person info updates to existed entry.
     * @param tabNo person identifier
     * @param jsonDiff differences of old user info and new one, json
     * @return the length of the list of changes
     */
    @Override
    public Long addPersonUpdate(String tabNo, JSONObject jsonDiff) {
        if (!connectTimeout()) {
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            lm.set(null);
            String key = PERSON_UPDATE_KEY_PREFIX + tabNo;
            JSONObject json = new JSONObject();
            json.put("time", System.currentTimeMillis());
            json.put("tabNo", tabNo);
            json.put("diff", jsonDiff);
            return jedis.rpush(key, json.toString());
        }
        catch (JedisConnectionException jce) {
            processConnectionError(jce);
        }
        catch (Exception ex) {
            lm.set(new ErrorMessage("addPersonUpdate error.", ex));
            log.error("addPersonUpdate error.", ex);
        }
        return null;
    }

    /**
     * Returns list of info updates for specified person.
     * @param tabNo person identifier
     * @return list of info updates
     */
    @Override
    public List<String> getPersonUpdate(String tabNo) {
        if (!connectTimeout()) {
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            lm.set(null);
            String key = PERSON_UPDATE_KEY_PREFIX + tabNo;
            return jedis.lrange(key, 0, -1);
        }
        catch (JedisConnectionException jce) {
            processConnectionError(jce);
        }
        catch (Exception ex) {
            lm.set(new ErrorMessage("getPersonUpdate error.", ex));
        }
        return null;
    }

    /**
     * Fast store methods don't returns exception if error occurred.
     * Application should check last error if such logic required.
     * @return last ErrorMessage or null;
     */
    @Override
    public ErrorMessage getLastError() {
        return lm.get();
    }

    /**
     * Returns flight report for specified person from redis.
     * @param personId person identifier
     * @return flight report for specified person, string
     */
    @Override
    public String findPersonFlightReport(Long personId) {
        if (!connectTimeout()) {
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            lm.set(null);
            return jedis.get(PERSON_FLIGHTS_KEY_PREFIX + personId);
        }
        catch (JedisConnectionException jce) {
            processConnectionError(jce);
        }
        catch (Exception ex) {
            lm.set(new ErrorMessage("findPersonFlightReport() error.", ex));
            log.error("findPersonFlightReport error", ex);
        }
        return null;
    }

    /**
     * Stores flight report for specified person into redis.
     * @param personId person identifier
     * @param value flight report, string
     */
    @Override
    public void storePersonFlightReport(Long personId, String value) {
        if (!connectTimeout()) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            lm.set(null);
            jedis.set(PERSON_FLIGHTS_KEY_PREFIX + personId, value);
        }
        catch (JedisConnectionException jce) {
            processConnectionError(jce);
        }
        catch (Exception ex) {
            lm.set(new ErrorMessage("storePersonFlightReport() error.", ex));
        }
    }

    /**
     * Drops flight report of specified person.
     * @param personId person identifier
     */
    @Override
    public void dropPersonFlightReport(Long personId) {
        if (!connectTimeout()) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            lm.set(null);
            jedis.del(PERSON_FLIGHTS_KEY_PREFIX + personId);
        }
        catch (JedisConnectionException jce) {
            processConnectionError(jce);
        }
        catch (Exception ex) {
            lm.set(new ErrorMessage("storePersonFlightReport() error.", ex));
        }
    }

    /**
     * In case of JedisConnectionException this method reset connErrorTime field and keep error.
     * @param jce occurred connection exception
     */
    private void processConnectionError(JedisConnectionException jce) {
        connErrorTime = System.currentTimeMillis();
        lm.set(new ErrorMessage("Redis connection error.", jce));
        log.warn("Redis connection error." + jce.getMessage());
    }

    /**
     * Returns full statistics for specified person from redis.
     * @param personId person identifier
     * @return full statistics for specified person, string
     */
    @Override
    public String findFullPersonStatistics(Long personId) {
        if (!connectTimeout()) {
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            lm.set(null);
            return jedis.get(PERSON_FULL_STATISTICS_KEY_PREFIX + personId);
        }
        catch (JedisConnectionException jce) {
            processConnectionError(jce);
        }
        catch (Exception ex) {
            lm.set(new ErrorMessage("findFullPersonStatistics() error.", ex));
            log.error("findFullPersonStatistics error", ex);
        }
        return null;
    }

    /**
     * Stores statistics for specified person into redis.
     * @param personId person identifier
     * @param value full statistics, string
     */
    @Override
    public void storeFullPersonStatistics(Long personId, String value) {
        if (!connectTimeout()) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            lm.set(null);
            jedis.set(PERSON_FULL_STATISTICS_KEY_PREFIX + personId, value);
        }
        catch (JedisConnectionException jce) {
            processConnectionError(jce);
        }
        catch (Exception ex) {
            lm.set(new ErrorMessage("findFullPersonStatistics() error.", ex));
            log.error("findFullPersonStatistics error", ex);
        }
    }
}
