package cselp.bean;

import cselp.domain.local.LogMessage;
import org.springframework.context.ApplicationEvent;

public class LogContainerEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never {@code null})
     */
    public LogContainerEvent(LogMessage source) {
        super(source);
    }

    public LogMessage getLogMessage() {
        return (LogMessage)getSource();
    }
}
