package cselp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ErrorMessage implements Serializable {

    private String message;
    private List<String> messages = new ArrayList<>();
    private Throwable cause;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getMessages() {
        return messages;
    }

    public Throwable getCause() {
        return cause;
    }
}
