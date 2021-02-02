package cselp.service;


import cselp.exception.RAException;

import java.util.Date;

/**
 * Interface of synchronization service. Links AirFASE flight to OpenSky flight.
 */
public interface IDataSyncService {

    /**
     * Parse OpenSky document, extract and store flights.
     * @param xml OpenSky document
     * @param fileName processed file name, used in generated log messages
     * @return result status message
     * @throws RAException if any
     */
    String parseFlights(String xml, String fileName) throws RAException;

    /**
     * Synchronize OpenSky and AirFASE flights.
     * OpenSky flights without links to AirFASE flights selects from database according specified from and to dates.
     * @param from from date
     * @param to to date
     * @throws RAException if any
     */
    void syncFlightsAndLegs(Date from, Date to) throws RAException;

    /**
     * Synchronize OpenSky and AirFASE flights.
     * Synchronization applied only to OpenSky flights, not linked to AirFASE flights,
     * and not older than due period (2 month)
     * @throws RAException if any
     */
    void syncFlightsAndLegs() throws RAException;

    /**
     * Updates scores for uncompleted EventScore dto.
     * @param maxResults maximum number of records to be updated
     * @return updated records count
     * @throws RAException  if any
     */
    int updateEventScores(Integer maxResults) throws RAException;
}
