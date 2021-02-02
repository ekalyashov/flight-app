package cselp.event;


import cselp.bean.LogContainerEvent;
import cselp.domain.local.LogMessage;
import cselp.service.local.ILFlightService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

/**
 * Class responsible for processing of LogContainerEvent events.
 * Distribute log messages among different log consumers, f.e. database, loggers, JMS.
 */
public class LogEventListener implements ApplicationListener<LogContainerEvent> {
    private static final Log log = LogFactory.getLog(LogEventListener.class);
    //private static final Log jmsLog = LogFactory.getLog("send_activemq_message");

    private IMessageProducer messageProducer;
    private ILFlightService lFlightService;

    public void setMessageProducer(IMessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public void setlFlightService(ILFlightService lFlightService) {
        this.lFlightService = lFlightService;
    }

    /**
     * Handle an LogContainerEvent event
     * @param event received event
     */
    @Override
    public void onApplicationEvent(LogContainerEvent event) {
        LogMessage message = event.getLogMessage();
        /*try {
            if (messageProducer != null) {
                messageProducer.sendMessage(message);
            }
        } catch (Exception e) {
            log.error("Error in onApplicationEvent", e);
        } */
        try {
            //save into DB
            if (lFlightService != null) {
                lFlightService.createLogMessage(message);
            }
        } catch (Exception e) {
            log.error("Error in onApplicationEvent", e);
        }
        /*try {
            jmsLog.info(message.getClassifier() + '|' + message.getErrorCode() + '|' + message.getLogTime() + '|' +
                message.getProperties());
        } catch (Exception e) {
            log.error("Error in onApplicationEvent", e);
        } */
    }
}
