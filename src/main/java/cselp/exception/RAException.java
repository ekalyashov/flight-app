package cselp.exception;


public class RAException extends Exception {
    public RAException() {
    }

    public RAException(String message) {
        super(message);
    }

    public RAException(String message, Throwable cause) {
        super(message, cause);
    }

    public RAException(Throwable cause) {
        super(cause);
    }

    public RAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
