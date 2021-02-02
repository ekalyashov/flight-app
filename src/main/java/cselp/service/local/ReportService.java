package cselp.service.local;


import cselp.bean.FlightSearchCondition;
import cselp.dao.local.IReportDao;
import cselp.domain.local.EventScore;
import cselp.domain.local.Leg;
import cselp.domain.local.Trip;
import cselp.domain.report.PersonFlight;
import cselp.domain.report.PersonLeg;
import cselp.exception.RADbException;
import cselp.exception.RAException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportService implements IReportService {

    private IReportDao dao;

    public void setDao(IReportDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PersonFlight> findPersonFlights(Long personId) throws RAException {
        return dao.findPersonFlights(personId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, List<EventScore>> findEventScoresMap(Collection<Long> flightIds) throws RAException {
        return dao.findEventScoresMap(flightIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, List<PersonLeg>> findPersonLegsOpt(FlightSearchCondition condition) throws RAException {
        return dao.findPersonLegsOpt(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, List<Trip>> findTrips(FlightSearchCondition condition) throws RAException {
        return dao.findTrips(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trip> findTrips(Date from, Date to) throws RAException {
        return dao.findTrips(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Leg getLeg(Long id) throws RADbException {
        return dao.getLeg(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Trip> getTripMap(Collection<Long> tripIds) throws RAException {
        return dao.getTripMap(tripIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Leg> findLastLegs(Long personId, Integer count) throws RAException {
        return dao.findLastLegs(personId, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Leg> findPersonLegs(Long personId) throws RAException {
        return dao.findPersonLegs(personId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventScore> findPersonEventScores(Long personId) throws RADbException {
        return dao.findPersonEventScores(personId);
    }
}
