package net.kemitix.binder.spi;

/**
 * Represents an exception that has occurred, but that a stack trace is not
 * required for.
 */
public class BinderException
        extends RuntimeException {
    public BinderException(String message, Exception cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        Throwable cause = getCause();
        if (cause != null) {
            sb.append(System.lineSeparator());
            sb.append(cause.getMessage());
        }
        return sb.toString();
    }
}
