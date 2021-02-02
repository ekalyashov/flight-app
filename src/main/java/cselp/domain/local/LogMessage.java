package cselp.domain.local;

import java.io.Serializable;
import java.sql.Timestamp;


public class LogMessage implements Serializable {

    private Long id;
    private String source;
    private String classifier;
    private String errorCode;
    private Timestamp logTime;
    private String properties;
    private String level;

    public LogMessage() {
    }

    public LogMessage(String classifier, String errorCode, String level, Timestamp logTime) {
        this.classifier = classifier;
        this.errorCode = errorCode;
        this.logTime = logTime;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    public void setLogTime(Timestamp logTime) {
        this.logTime = logTime;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
