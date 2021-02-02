package cselp.service;


import cselp.bean.LogContainerEvent;
import cselp.domain.local.LoadedFlight;
import cselp.exception.RAException;
import cselp.service.local.ILFlightService;
import cselp.util.DataUtil;
import cselp.Const;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Class implements task to load OpenSky xml documents, parse its and store flights into database.
 * Parsed files then compressed and moved to correspondent archive folders.
 * Task execution rules defined in spring context definitions.
 */
public class LoadFlightsDataTask implements ApplicationContextAware, ILoadFlightsDataTask {
    private static final Log log = LogFactory.getLog(LoadFlightsDataTask.class);

    static final int BUFFER_SIZE = 2048;
    static final long MEGABYTE = 1024 * 1024;

    private Configuration appConfig;
    private ApplicationContext appContext;
    private ILFlightService flightService;
    private IDataSyncService dataSyncService;

    public void setAppProperties(Properties appProperties) {
        appConfig = ConfigurationConverter.getConfiguration(appProperties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public void setFlightService(ILFlightService flightService) {
        this.flightService = flightService;
    }

    public void setDataSyncService(IDataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    /**
     * Method searches xml files in predefined folder. If file already processed (duplicated), it is ignored.
     * Then method parse file, store flights info into database, compress processed file and move to archive folders.
     * After all files processed, free disc space checked, warning messages generated if disk is full.
     */
    @Override
    public void runLoadFlightsFiles() {
        try {
            log.info("====Start load process");
            Boolean skipLoadFiles = appConfig.getBoolean("load.flights.skip.loading", false);
            Boolean moveProcessedFiles = appConfig.getBoolean("load.flights.move.processed.files", true);
            if (skipLoadFiles) {
                log.warn("Skip load process, parameter 'load.flights.skip.loading' is true");
                return;
            }
            String path = appConfig.getString("load.flights.initial.path");
            String fileEncoding = appConfig.getString("load.flights.file.encoding", "windows-1251");
            String filePattern = appConfig.getString("load.flights.file.pattern", "*.*");
            long lowDiskSpace = appConfig.getInt("load.flights.free.disk.space.low", 20) * MEGABYTE;
            long criticalDiskSpace = appConfig.getInt("load.flights.free.disk.space.critical", 20) * MEGABYTE;
            File loadDir = new File(path);
            if (!loadDir.exists() || !loadDir.isDirectory()) {
                log.error("Path " + path + " was not found or isn't directory");
                return;
            }
            FileFilter fileFilter = new WildcardFileFilter(filePattern, IOCase.INSENSITIVE);
            File[] files = loadDir.listFiles(fileFilter);
            if (files == null) {
                log.error("listFiles error, result is null.");
            }
            else {
                for (File file : files) {
                    try {
                        if (isFileNew(file)) {
                            parseFile(file, fileEncoding);
                        }
                        else {
                            log.warn("File " + file.getName() + " already loaded, ignore.");
                        }
                    } catch (Exception e) {
                        log.error("Error parsing file " + file.getName(), e);
                        appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                                Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.IO_ERROR, Level.ERROR, e)));
                    }
                    finally {
                        //compress files, move to Archive dir
                        if (moveProcessedFiles) {
                            compressFile(file);
                        }
                        else {
                            log.info("Processed file " + file.getName() + " do not moved to archive.");
                        }
                    }
                }
            }
            validateFreeSpace(lowDiskSpace, criticalDiskSpace, loadDir);
        } catch (Throwable t) {
            log.error("LoadFlightsDataTask error", t);
            appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                    Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.IO_ERROR, Level.ERROR, t)));
        }
    }

    /**
     * Checks free disk space for low and critical levels, generates corresponding messages.
     * @param lowDiskSpace low Disk Space level
     * @param criticalDiskSpace critical Disk Space level
     * @param loadDir folder used to check low space
     */
    private void validateFreeSpace(long lowDiskSpace, long criticalDiskSpace, File loadDir) {
        long freeSpace = loadDir.getFreeSpace();
        if (freeSpace < criticalDiskSpace) {
            log.error("Disk space critical : " + freeSpace);
            Map<String, String> props = new HashMap<>();
            props.put("left", String.format("%.2f", ((double) freeSpace) / MEGABYTE));
            appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                    Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.ARCHIVE_NO_SPACE, Level.ERROR, props)));
        }
        else if (freeSpace < lowDiskSpace) {
            log.warn("Low disk space : " + freeSpace);
            Map<String, String> props = new HashMap<>();
            props.put("left", String.format("%.2f", ((double)freeSpace)/ MEGABYTE));
            appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                    Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.ARCHIVE_LOW_SPACE, Level.WARN, props)));
        }
    }

    /**
     * Checks was this file already loaded and parsed or is new.
     * @param file specified file
     * @return true if file is new, false otherwise
     * @throws RAException if any
     */
    @Override
    public boolean isFileNew(File file) throws RAException {
        List<LoadedFlight> loadedFlights = flightService.findLoadedFlights(file.getName());
        return loadedFlights.isEmpty();
    }

    /**
     * Process OpenSky xml document, extract and store flights, persons, crews info into database.
     * @param file xml document
     * @param fileEncoding fileEncoding
     * @throws RAException if any
     * @throws IOException if I/O error occurred
     */
    @Override
    public void parseFile(File file, String fileEncoding) throws RAException, IOException {
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        String xml = FileUtils.readFileToString(file, fileEncoding);
        try {
            String message = dataSyncService.parseFlights(xml, file.getName());
            try {
                LoadedFlight lf = new LoadedFlight(file.getName(), startTime,
                        new Timestamp(System.currentTimeMillis()), "SUCCESS", message);
                flightService.createLoadedFlight(lf);
            } catch (Exception e) {
                log.error("createLoadedFlight error", e);
                appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                        Const.LogClassifier.GENERAL, Const.LogCode.UNKNOWN_ERROR, Level.ERROR, e)));
            }
        } catch (RAException rae) {
            String message = getExceptionString(rae);
            LoadedFlight lf = new LoadedFlight(file.getName(), startTime,
                    new Timestamp(System.currentTimeMillis()), "ERROR", message);
            flightService.createLoadedFlight(lf);
        }
    }

    private String getExceptionString(RAException rae) {
        String message = "";
        try {
            message = rae.getLocalizedMessage();
            if (rae.getCause() != null) {
                message = (message == null) ? rae.getCause().getLocalizedMessage() :
                        message + " \n" + rae.getCause().getLocalizedMessage();
                message += " \n" + rae.getCause().getClass().getName();
                StackTraceElement[] str = rae.getCause().getStackTrace();
                int size = Math.min(str.length, 5);
                for (int i = 0; i < size; i++) {
                    message += "   \n" + str[i].toString();
                }
                if (message.length() > 1024) {
                    message = message.substring(0, 1024);
                }
            }
        } catch (Exception e) {
            log.error("Error  to extract exception string", e);
            message = e.getLocalizedMessage() + " - " + e.getClass().getName();
        }
        return message;
    }

    /**
     * Compress file using zip format and move it into archive folder
     * @param file target file
     * @throws IOException if any
     */
    private void compressFile(File file) throws IOException {
        String fileSeparator = System.getProperty("file.separator");
        String archivePath = appConfig.getString("load.flights.archive.path");
        String zipFileName = archivePath + fileSeparator + file.getName() + ".zip";
        Matcher m = Pattern.compile("[0-9]{8}").matcher(file.getName());
        if (m.find()) {
            String token = m.group();
            String sYear = token.substring(0, 4);
            String sMonth = token.substring(4, 6);
            //String sDay = token.substring(6, 8);
            String path = appConfig.getString("load.flights.initial.path");
            File loadDir = new File(path);
            File archiveDir = new File(archivePath);
            if (!loadDir.exists() || !loadDir.isDirectory()) {
                log.error("Path " + path + " was not found or isn't directory");
            }
            if (!archiveDir.exists() || !archiveDir.isDirectory()) {
                log.error("Archive path " + path + " was not found or isn't directory");
            }
            String yearPath = archivePath + fileSeparator + sYear;
            boolean success = checkAndCreateDirectory(yearPath);
            if (!success) {
                Map<String, String> props = new HashMap<>();
                props.put("message", "Can not create folder '" + yearPath + "', use default '" + archivePath + "'");
                appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                        Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.ARCHIVE_ERROR, Level.ERROR, props)));
            }
            else {
                String monthPath = yearPath + fileSeparator + sMonth;
                success = checkAndCreateDirectory(monthPath);
                if (!success) {
                    Map<String, String> props = new HashMap<>();
                    props.put("message", "Can not create folder '" + monthPath + "', use default '" + archivePath + "'");
                    appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                            Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.ARCHIVE_ERROR, Level.ERROR, props)));
                }
                zipFileName = monthPath + fileSeparator + file.getName() + ".zip";
            }
        }
        else {
            String message = "Invalid file name " + file.getName() +
                    ", could not define file date, use default path '" + archivePath + "'";
            log.warn(message);
            Map<String, String> props = new HashMap<>();
            props.put("message", message);
            appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                    Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.ARCHIVE_ERROR, Level.ERROR, props)));
        }
        compressFile(file, zipFileName);
        boolean success = file.delete();
        if (!success) {
            log.warn("File " + file.getAbsolutePath() + " deletion failed.");
        }
    }

    /**
     * Compress input file and store using specified output name
     * @param file input file
     * @param zipFileName output file name
     * @throws IOException if any
     */
    private void compressFile(File file, String zipFileName) throws IOException {
        byte data[] = new byte[BUFFER_SIZE];
        try (FileOutputStream dest = new FileOutputStream(zipFileName);
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
             FileInputStream fi = new FileInputStream(file);
             BufferedInputStream origin = new BufferedInputStream(fi, BUFFER_SIZE)) {
            ZipEntry entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                out.write(data, 0, count);
            }
        }
    }

    /**
     * Checks if specified folder exists, create new if not exists
     * @param name full folder name
     * @return true if folder exists or created, false otherwise
     */
    private boolean checkAndCreateDirectory(String name) {
        File dir = new File(name);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                //creation error
                log.error("Can not create archive directory " + dir.getAbsolutePath());
                return false;
            }
        }
        else if (!dir.isDirectory()) {
            log.error("Another file with name " + dir.getAbsolutePath() + " exists and isn't directory");
            return false;
        }
        return true;
    }
}
