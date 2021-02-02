package cselp.event;


import cselp.Const;
import cselp.bean.LogContainerEvent;
import cselp.bean.PersonContainerEvent;
import cselp.domain.local.Person;
import cselp.service.report.IReportComposer;
import cselp.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

public class PersonsEventListener implements ApplicationListener<PersonContainerEvent>, ApplicationContextAware {
    private static final Log log = LogFactory.getLog(PersonsEventListener.class);

    private IReportComposer reportComposer;
    private ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public void setReportComposer(IReportComposer reportComposer) {
        this.reportComposer = reportComposer;
    }

    /**
     * Process PersonContainerEvent event, updates person statistic stored into external cache (Redis)
     * @param event PersonContainerEvent object
     */
    @Override
    public void onApplicationEvent(PersonContainerEvent event) {
        //refresh all reports for specified persons
        long start = System.currentTimeMillis();
        String ids = event.getPersons().size() + " reports updated for persons ";
        for (Person p : event.getPersons()) {
            try {
                reportComposer.updateFullPersonStatistics(p);
                ids += p.getId() + ",";
            }
            catch (Exception e) {
                log.error("Reports update for person " + p.getId() + " failed");
                Map<String, String> properties = new HashMap<>();
                properties.put("personId", String.valueOf(p.getId()));
                appContext.publishEvent(new LogContainerEvent(DataUtil.createLogMessage(
                        Const.LogClassifier.REPORT_UPDATE, Const.LogCode.UNKNOWN_ERROR, Level.WARN, e, properties)));
            }
        }
        ids += "\n time " + (System.currentTimeMillis() - start);
        log.info(ids);
    }

}
