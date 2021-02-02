package cselp.service;

import cselp.exception.RAException;

import java.io.File;
import java.io.IOException;

public interface ILoadFlightsDataTask {
    /**
     * Starts processing of new Flights files
     */
    void runLoadFlightsFiles();

    /**
     * Checks was this file already loaded and parsed or is new.
     * @param file specified file
     * @return true if file is new, false otherwise
     * @throws RAException if any
     */
    boolean isFileNew(File file) throws RAException;

    /**
     * Process OpenSky xml document, extract and store flights, persons, crews info into database.
     * @param file xml document
     * @param fileEncoding fileEncoding
     * @throws RAException if any
     * @throws IOException if I/O error occurred
     */
    void parseFile(File file, String fileEncoding) throws RAException, IOException;
}
