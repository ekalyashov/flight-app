package cselp.dao;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of data selection from database by identifiers collection,
 * data selected by batch, until all identifiers used.
 * When using IN operator, parameter count in sql is limited.
 * @param <T>
 */
public abstract class DataByIdsFiller<T> implements IDataFiller<T> {
    int batchSize = 1;
    final List<Long> ids = new ArrayList<>();

    public DataByIdsFiller(int batchSize, Collection<Long> ids) {
        this.batchSize = batchSize;
        this.ids.addAll(ids);
    }

    @Override
    public boolean hasNext() {
        return !ids.isEmpty();
    }

    private Collection<Long> getBatchIds() {
        Iterator<Long> it = ids.iterator();
        List<Long> batch = new ArrayList<>();
        while (it.hasNext() && batch.size() < batchSize) {
            Long locationId = it.next();
            it.remove();
            batch.add(locationId);
        }
        return batch;
    }

    @Override
    public Collection<T> next() {
        Collection<Long> batchIds = getBatchIds();
        return getQueryResult(batchIds);
    }

    /**
     * Abstract method, should be implemented in subclass
     * @param batchIds collection of identifiers
     * @return Collection of selected entities
     */
    protected abstract Collection<T> getQueryResult(Collection<Long> batchIds);
}

