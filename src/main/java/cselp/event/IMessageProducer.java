package cselp.event;


import cselp.domain.local.LogMessage;
import org.apache.log4j.spi.LoggingEvent;

import javax.jms.JMSException;

public interface IMessageProducer {
    void sendMessage(String msg) throws JMSException;

    void sendMessage(LoggingEvent msg) throws JMSException;

    void sendMessage(LogMessage msg) throws JMSException;
}
