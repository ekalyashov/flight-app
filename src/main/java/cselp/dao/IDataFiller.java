package cselp.dao;

import java.util.Collection;

/**
 * Interface to realize data selection from database by several parts,
 * f.e. when using IN operator, parameter count in sql is limited.
 */
public interface IDataFiller<T> {

    Collection<T> next();

    boolean hasNext();
}
