package net.kemitix.binder.app;

public class ManuscriptFormatException extends RuntimeException {

    public ManuscriptFormatException(String message) {
        super(message);
    }

    public ManuscriptFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
