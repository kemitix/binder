package net.kemitix.binder.spi;

import java.io.File;

public class ManuscriptFormatException extends RuntimeException {

    private final Class<?> theRoot;
    private final File file;

    public ManuscriptFormatException(String message, Class<?> theRoot, File file, Throwable cause) {
        super(message, cause);
        this.theRoot = theRoot;
        this.file = file;
    }

    public <T> ManuscriptFormatException(String message, Class<T> theRoot, File file) {
        super(message);
        this.theRoot = theRoot;
        this.file = file;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        sb.append(": ");
        sb.append(theRoot);
        sb.append(" from ");
        sb.append(file);
        Throwable cause = getCause();
        if (cause != null) {
            sb.append(System.lineSeparator());
            sb.append(cause.getMessage());
        }
        return sb.toString();
    }
}
