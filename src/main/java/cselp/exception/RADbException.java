package cselp.exception;

public class RADbException extends RAException {

    public RADbException() {
    }

    public RADbException(String message) {
        super(message);
    }

    public RADbException(String message, Throwable cause) {
        super(message, cause);
    }

    public RADbException(Throwable cause) {
        super(cause);
    }

    public RADbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
