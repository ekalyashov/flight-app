package cselp;


import cselp.bean.LogContainerEvent;
import cselp.domain.local.Leg;
import cselp.exception.RAException;
import cselp.service.IDataSyncService;
import cselp.service.ILoadFlightsDataTask;
import cselp.service.ldap.ILdapService;
import cselp.service.local.ILFlightService;
import cselp.util.DataUtil;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standalone application for service purposes. OpenSky xml documents for specified period,
 * from arbitrary folder can be parsed, flight synchronization for specified period started.
 * Ensures bulk loading and synchronization of archived or old data.
 */
public class Application {
    private static final Log log = LogFactory.getLog(Application.class);

    private static final String usage =
            "Usage : java -cp .;./cselp.jar;./lib/* Application [command] [parameters]\n" +
            " command\n" +
            " load\n" +
            "    parameters [-file={fileName}] [-loadDir={loadDir}] [-from={dateFrom} -to={dateTo}]\n" +
            " sync\n    parameters [-from={dateFrom}] [-to={dateTo}]\n" +
            " date format yyyy-MM-dd";

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private Properties appConfig;
    private ClassPathXmlApplicationContext context;
    private String path;
    private String fileEncoding;
    private String filePattern;

    public static void main(String[] args) {
        Application app = new Application();
        try {
            String command = "";
            if (args.length > 0) {
                command = args[0];
            }
            app.init();
            boolean success = app.execCommand(command, args);
            if (success) {
                log.debug("Command " + command + " success.");
                System.out.println("Command " + command + " success.");
            }
            else {
                System.out.println(usage);
            }
            log.info("Delay for event processing");
            Thread.sleep(1000);
            //System.exit(0);
        } catch (Exception e) {
            log.error("Application error", e);
            //System.exit(1);
        }
        finally {
            app.shutdown();
        }
    }

    private boolean execCommand(String command, String[] args) throws RAException, IOException {
        if ("load".equalsIgnoreCase(command)) {
            log.info("load Command started");
            String fileName = getParameter(args, "-file=");
            if (fileName != null) {
                //load file
                return loadFile(fileName);
            }
            else {
                String loadDir = getParameter(args, "-loadDir=");
                if (loadDir == null) {
                    loadDir = path;
                }
                Date dateFrom = parseDate(getParameter(args, "-from="));
                Date dateTo = parseDate(getParameter(args, "-to="));
                if (dateFrom != null && dateTo != null) {
                    //load files according dates
                    return loadFileList(loadDir, dateFrom, dateTo);
                }
                else {
                    System.out.println("load Command, load folder, -from and -to parameters required");
                }
            }
        }
        else if ("sync".equalsIgnoreCase(command)) {
            log.info("sync Command started");
            IDataSyncService dataSyncService = context.getBean("dataSyncService", IDataSyncService.class);
            Date dateFrom = parseDate(getParameter(args, "-from="));
            Date dateTo = parseDate(getParameter(args, "-to="));
            if (dateFrom != null && dateTo != null) {
                log.info("syncFlightsAndLegs from " + dateFrom + " to " + dateTo);
                dataSyncService.syncFlightsAndLegs(dateFrom, dateTo);
                return true;
            }
            else {
                System.out.println("sync Command, -from and -to parameters required");
            }
        }
        else if ("test".equalsIgnoreCase(command)) {
            return testCommand(args);
        }
        return false;
    }

    private boolean testCommand(String[] args) throws RAException {
        String fileName = getParameter(args, "-file=");
        log.info("test Command, file : " + fileName);
        if (fileName != null) {
            File file = getFile(fileName);
            if (file != null) {
                ILoadFlightsDataTask lf = context.getBean("loadFlightsDataTask", ILoadFlightsDataTask.class);
                if (lf.isFileNew(file)) {
                    log.info("File " + fileName + " is new.");
                    return true;
                } else {
                    log.info("File " + fileName + " is processed or not exists.");
                    return true;
                }
            }
            else {
                log.info("File " + fileName + " is not exists.");
                return true;
            }
        }
        else {
            String loadDir = getParameter(args, "-loadDir=");
            if (loadDir == null) {
                loadDir = path;
            }
            log.info("test Command, loadDir : " + loadDir);
            Date dateFrom = parseDate(getParameter(args, "-from="));
            Date dateTo = parseDate(getParameter(args, "-to="));
            if (dateFrom != null && dateTo != null) {
                File[] files = getFileList(loadDir);
                if (files == null) {
                    log.info("listFiles error, result is null.");
                    return false;
                }
                else {
                    List<String> fileList = new ArrayList<>();
                    for (File file : files) {
                        //check date
                        Date fileDate = getFileDate(file.getName());
                        if (fileDate == null || fileDate.before(dateFrom) || fileDate.after(dateTo)) {
                            //ignore such file
                            continue;
                        }
                        fileList.add(file.getName());
                    }
                    String message = "Found files :\n";
                    for (String name : fileList) {
                        message += name + "\n";
                    }
                    log.info(message);
                    return true;
                }
            }
            else return false;
        }
    }

    private boolean loadFileList(String loadDir, Date dateFrom, Date dateTo) {
        log.info("load file list from" + loadDir);
        File[] files = getFileList(loadDir);
        if (files == null) {
            log.info("listFiles error, result is null.");
            return false;
        }
        else {
            ILoadFlightsDataTask lf =context.getBean("loadFlightsDataTask", ILoadFlightsDataTask.class);
            for (File file : files) {
                //check date
                Date fileDate = getFileDate(file.getName());
                if (fileDate == null || fileDate.before(dateFrom) || fileDate.after(dateTo)) {
                    //ignore such file
                    continue;
                }
                try {
                    if (lf.isFileNew(file)) {
                        lf.parseFile(file, fileEncoding);
                    }
                    else {
                        log.info("File " + file.getName() + " already loaded, ignore.");
                    }
                } catch (Exception e) {
                    context.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                            Const.LogClassifier.OS_FLIGHT_LOAD, Const.LogCode.IO_ERROR, Level.ERROR, e)));
                }
            }
            return true;
        }
    }

    private File[] getFileList(String loadDir) {
        File loadFolder = new File(loadDir);
        File[] files = null;
        if (!loadFolder.exists() || !loadFolder.isDirectory()) {
            log.info("Path " + path + " was not found or isn't directory");
        }
        else {
            FileFilter fileFilter = new WildcardFileFilter(filePattern, IOCase.INSENSITIVE);
            files = loadFolder.listFiles(fileFilter);
        }
        return files;
    }

    private boolean loadFile(String fileName) throws RAException, IOException {
        log.info("load file " + fileName);
        File file = getFile(fileName);
        if (file != null) {
            ILoadFlightsDataTask lf =context.getBean("loadFlightsDataTask", ILoadFlightsDataTask.class);
            if (lf.isFileNew(file)) {
                lf.parseFile(file, fileEncoding);
            }
            else {
                log.info("File " + file.getName() + " already loaded, ignore.");
            }
            return true;
        }
        return false;
    }

    private void init() {
        context = new ClassPathXmlApplicationContext("WEB-INF/config/spring/context-app.xml");
        appConfig = context.getBean("appConfig", Properties.class);
        path = appConfig.getProperty("load.flights.initial.path");
        fileEncoding = appConfig.getProperty("load.flights.file.encoding", "windows-1251");
        filePattern = appConfig.getProperty("load.flights.file.pattern", "*.*");
    }

    private void shutdown() {
        if (context != null) {
            context.close();
        }
    }

    private File getFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private String getParameter(String[] args, String prefix) {
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return arg.substring(prefix.length());
            }
        }
        return null;
    }

    private Date parseDate(String sDate) {
        try {
            return df.parse(sDate);
        }
        catch (Exception e) {
            //suppress exception
        }
        return null;
    }

    private Date getFileDate(String fileName) {
        try {
            Matcher m = Pattern.compile("[0-9]{8}").matcher(fileName);
            if (m.find()) {
                String token = m.group();
                String sYear = token.substring(0, 4);
                String sMonth = token.substring(4, 6);
                String sDay = token.substring(6, 8);
                return buildDate(sYear, sMonth, sDay);
            }
            else {
                m = Pattern.compile("[0-9]{6}").matcher(fileName);
                if (m.find()) {
                    String token = m.group();
                    String sYear = token.substring(0, 4);
                    String sMonth = token.substring(4, 6);
                    String sDay = "1";
                    return buildDate(sYear, sMonth, sDay);
                }
            }
        }
        catch (Exception e) {
            //suppress exception
            log.error("File name " + fileName + " parsing error.", e);
        }
        return null;
    }

    private Date buildDate(String sYear, String sMonth, String sDay) {
        int year = Integer.parseInt(sYear);
        int month = Integer.parseInt(sMonth) - 1; //0 - based
        int day = Integer.parseInt(sDay);
        Calendar cld = Calendar.getInstance();
        cld.set(year, month, day, 0, 0, 0);
        return cld.getTime();
    }

    public void testApp() throws Exception {
        System.out.println("Application test");
        ApplicationContext context = new ClassPathXmlApplicationContext("WEB-INF/config/spring/context-app.xml");
        ILdapService ldapService = context.getBean("ldapService", ILdapService.class);
        System.out.println(ldapService.getUsers());
        ILFlightService fService = context.getBean("lFlightService", ILFlightService.class);
        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.DATE, -15);
        List<Leg> legs = fService.findUnlinkedLegs(new Timestamp(cld.getTimeInMillis()));
        System.out.println("Found " + legs.size() + " unlinked legs");
    }
}
