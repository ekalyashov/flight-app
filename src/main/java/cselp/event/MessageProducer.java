package cselp.event;


import cselp.domain.local.LogMessage;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

public class MessageProducer implements IMessageProducer {

    private JmsTemplate jmsTemplate;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void sendMessage(final String msg) throws JMSException {
        jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(msg);
                return message;
            }
        });
    }

    @Override
    public void sendMessage(final LoggingEvent msg) throws JMSException {
        jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage message = session.createObjectMessage(msg);
                return message;
            }
        });
    }

    @Override
    public void sendMessage(final LogMessage msg) throws JMSException {
        jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(msg);
            }
        });
    }
}
