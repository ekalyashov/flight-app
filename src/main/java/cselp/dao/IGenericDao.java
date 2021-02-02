package cselp.dao;


import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.List;

/**
 * Interface contains method to realise base perisstent storage operations.
 */
public interface IGenericDao {

    /**
     * Getter, returns hibernate session factory.
     * @return hibernate session factory
     */
    SessionFactory getSessionFactory();

    <T, ID extends Serializable> T get(String entityName, ID id);

    <T> T save(String entityName, T entity);

    <T> T create(String entityName, T entity);

    <T> T update(String entityName, T entity);

    <T> List<T> findByNamedQuery(String queryName, Object... args);

    <T> List<T> findByNamedQuery(Integer maxResults, String queryName, Object... args);

    <T> T findUniqueResult(String queryName, String[] paramNames, Object[] values);

    <T> List<T> findByNamedQueryAndNamedParam(String queryName,
                                              String[] paramNames, Object[] values);

    <T> List<T> findByNamedQueryAndNamedParam(String queryName,
                                              String[] paramNames, Object[] values, Type[] types);

    <T> List<T> findByNamedQueryAndNamedParam(String queryName,
                                              String param, Object value);

    <T> List<T> findByNamedQueryAndNamedParam(Integer maxResults, String queryName,
                                              String[] paramNames, Object[] values);

    <T> List<T> findByNamedQueryAndNamedParam(Integer maxResults, String queryName,
                                              String[] paramNames, Object[] values, Type[] types);

    void flushSession();

    void clearSession();

    void evict(Object entity);
}
