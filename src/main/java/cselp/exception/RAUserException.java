package cselp.exception;


public class RAUserException extends RAException {
    public RAUserException() {
    }

    public RAUserException(String message) {
        super(message);
    }

    public RAUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public RAUserException(Throwable cause) {
        super(cause);
    }

    public RAUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
