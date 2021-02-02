package cselp.dao;

import org.hibernate.Query;
import org.hibernate.QueryParameterException;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Generic class to implements base data access object.
 */
public class GenericDao implements IGenericDao {

    protected SessionFactory sessionFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected String[] params(String... value) {
        return value;
    }

    protected Object[] args(Object... value) {
        return value;
    }

    protected Type[] types(Type... value) {
        return value;
    }

    protected void setParameter(Query q, int i, String[] paramNames, Object[] values) {
        Object value = values[i];
        if (value == null) {
            throw new QueryParameterException("Parameter " + paramNames[i] + " is null");
        }
        else {
            Class retType = value.getClass();
            if (Collection.class.isAssignableFrom(retType)) {
                q.setParameterList(paramNames[i], (Collection) value);
            } else if (retType.isArray()) {
                q.setParameterList(paramNames[i], (Object[]) value);
            } else {
                q.setParameter(paramNames[i], values[i]);
            }
        }
    }

    protected void setParameter(Query q, int i, String[] paramNames, Object[] values, Type[] types) {
        Object value = values[i];
        if (value == null) {
            q.setParameter(paramNames[i], value, types[i]);
        }
        else {
            Class retType = value.getClass();
            if (Collection.class.isAssignableFrom(retType)) {
                q.setParameterList(paramNames[i], (Collection) value);
            } else if (retType.isArray()) {
                q.setParameterList(paramNames[i], (Object[]) value);
            } else {
                q.setParameter(paramNames[i], values[i], types[i]);
            }
        }
    }

    /**
     * Loads domain object by it's entity name and unique id
     * @param entityName entity name to load
     * @param id unique ID of the domain object
     * @return instance of domain inject or null if not found
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, ID extends Serializable> T get(String entityName, ID id) {
        return (T)sessionFactory.getCurrentSession().get(entityName, id);
    }

    /**
     * Save or update the given persistent instance
     * @param entityName the name of the persistent entity
     * @param entity the persistent instance to save or update
     * @return updated entity
     */
    @Override
    public <T> T save(String entityName, T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entityName, entity);
        return entity;
    }

    /**
     * Create the given persistent instance
     * @param entityName the name of the persistent entity
     * @param entity the persistent instance to save or update
     * @return persisted entity
     */
    @Override
    public <T> T create(String entityName, T entity) {
        sessionFactory.getCurrentSession().persist(entityName, entity);
        return entity;
    }

    /**
     * Update the given persistent instance
     * @param entityName the name of the persistent entity
     * @param entity the persistent instance to save or update
     * @return persisted entity
     */
    @Override
    public <T> T update(String entityName, T entity) {
        sessionFactory.getCurrentSession().update(entityName, entity);
        return entity;
    }

    /**
     * Execute a named query binding a number of values to "?" parameters
     * in the query string.
     * @param queryName he name of a Hibernate query in a mapping file
     * @param args he values of the parameters
     * @return a List, containing the results of the query execution
     */
    @Override
    public <T> List<T> findByNamedQuery(String queryName, Object... args) {
        return findByNamedQuery(null, queryName, args);
    }

    /**
     * Execute a named query binding a number of values to "?" parameters
     * in the query string. If 'maxResults' value defined,
     * limits returned resultset size to specified value.
     * @param maxResults resultset size limit
     * @param queryName the name of a Hibernate query in a mapping file
     * @param args the values of the parameters
     * @return a List, containing the results of the query execution
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findByNamedQuery(Integer maxResults, String queryName, Object... args) {
        Query q = sessionFactory.getCurrentSession().getNamedQuery(queryName);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                q.setParameter(i, args[i]);
            }
        }
        if (maxResults != null) {
            q.setMaxResults(maxResults);
        }
        return q.list();
    }

    /**
     * Execute a named query, binding a number of values to ":" named parameters
     * in the query string.
     * @param queryName the name of a Hibernate query in a mapping file
     * @param paramNames the names of the parameters
     * @param values the values of the parameters
     * @return unique result of the query execution or null
     * throws NonUniqueResultException if there is more than one matching result
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T findUniqueResult(String queryName, String[] paramNames, Object[] values) {
        Query q = sessionFactory.getCurrentSession().getNamedQuery(queryName);
        if (paramNames != null && values != null) {
            for (int i = 0; i < paramNames.length; i++) {
                setParameter(q, i, paramNames, values);
            }
        }
        return (T)q.uniqueResult();
    }

    /**
     * Execute a named query, binding a number of values to ":" named parameters
     * in the query string.
     * @param queryName the name of a Hibernate query in a mapping file
     * @param paramNames the names of the parameters
     * @param values the values of the parameters
     * @return a List, containing the results of the query execution
     */
    @Override
    public <T> List<T> findByNamedQueryAndNamedParam(String queryName,
                                                     String[] paramNames, Object[] values) {
        return findByNamedQueryAndNamedParam(null, queryName, paramNames, values);
    }

    /**
     * Execute a named query, binding a number of values to ":" named parameters
     * in the query string.
     * @param queryName the name of a Hibernate query in a mapping file
     * @param paramNames the names of the parameters
     * @param values the values of the parameters
     * @param types the types of the parameters
     * @return a List, containing the results of the query execution
     */
    @Override
    public <T> List<T> findByNamedQueryAndNamedParam(String queryName,
                                                     String[] paramNames, Object[] values, Type[] types) {
        return findByNamedQueryAndNamedParam(null, queryName, paramNames, values, types);
    }

    /**
     * Returns a named query, with bounded named parameters.
     * @param queryName the name of a Hibernate query in a mapping file
     * @param paramNames the names of the parameters
     * @param values the values of the parameters
     * @return a query object
     */
    public Query getNamedQueryWithNamedParam(String queryName,
                                             String[] paramNames, Object[] values) {
        Query q = sessionFactory.getCurrentSession().getNamedQuery(queryName);
        if (paramNames != null && values != null) {
            for (int i = 0; i < paramNames.length; i++) {
                setParameter(q, i, paramNames, values);
            }
        }
        return q;
    }

    /**
     * Execute a named query, binding one value to a ":" named parameter
     * in the query string.
     * @param queryName the name of a Hibernate query in a mapping file
     * @param param the name of the parameter
     * @param value the value of the parameter
     * @return a List, containing the results of the query execution
     */
    @Override
    public <T> List<T> findByNamedQueryAndNamedParam(String queryName,
                                                     String param, Object value) {
        return findByNamedQueryAndNamedParam(null, queryName, params(param), args(value));
    }

    /**
     * Execute a named query, binding a number of values to ":" named parameters
     * in the query string. If 'maxResults' value defined,
     * limits returned resultSet size to specified value.
     * @param maxResults resultSet size limit
     * @param queryName the name of a Hibernate query in a mapping file
     * @param paramNames the names of the parameters
     * @param values the values of the parameters
     * @return a List, containing the results of the query execution
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findByNamedQueryAndNamedParam(Integer maxResults, String queryName,
                                                     String[] paramNames, Object[] values) {
        Query q = sessionFactory.getCurrentSession().getNamedQuery(queryName);
        if (paramNames != null && values != null) {
            for (int i = 0; i < paramNames.length; i++) {
                setParameter(q, i, paramNames, values);
            }
        }
        if (maxResults != null) {
            q.setMaxResults(maxResults);
        }
        return q.list();
    }

    /**
     * Execute a named query, binding a number of values to ":" named parameters
     * in the query string, using parameter types. If 'maxResults' value defined,
     * limits returned resultSet size to specified value.
     * @param maxResults resultSet size limit
     * @param queryName the name of a Hibernate query in a mapping file
     * @param paramNames the names of the parameters
     * @param values the values of the parameters
     * @param types the types of the parameters
     * @return a List, containing the results of the query execution
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findByNamedQueryAndNamedParam(Integer maxResults, String queryName,
                                                     String[] paramNames, Object[] values, Type[] types) {
        Query q = sessionFactory.getCurrentSession().getNamedQuery(queryName);
        if (paramNames != null && values != null) {
            for (int i = 0; i < paramNames.length; i++) {
                setParameter(q, i, paramNames, values, types);
            }
        }
        if (maxResults != null) {
            q.setMaxResults(maxResults);
        }
        return q.list();
    }

    /**
     * Force current session to flush.
     */
    @Override
    public void flushSession() {
        sessionFactory.getCurrentSession().flush();
    }

    /**
     * Completely clear current session. Evict all loaded instances and cancel all pending
     * saves, updates and deletions.
     */
    @Override
    public void clearSession() {
        sessionFactory.getCurrentSession().clear();
    }

    /**
     * Remove specified entity from the session cache.
     * @param entity The entity to evict
     */
    @Override
    public void evict(Object entity) {
        sessionFactory.getCurrentSession().evict(entity);
    }
}
