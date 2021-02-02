package cselp.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class implements task to synchronize flights (legs) from OpenSky documents and AirFASE flights.
 * Task execution rules defined in spring context definitions.
 */
public class SyncFlightsAndLegsTask implements ISyncFlightsAndLegsTask {
    private static final Log log = LogFactory.getLog(SyncFlightsAndLegsTask.class);

    private IDataSyncService dataSyncService;

    public void setDataSyncService(IDataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runSyncFlightsAndLegs() {
        try {
            log.info("====Start sync process");
            //dataSyncService.syncFlightsAndLegs();
            dataSyncService.syncFlightsAndLegs();
        } catch (Throwable t) {
            log.error("SyncFlightsAndLegs Task error", t);
        }
    }
}
