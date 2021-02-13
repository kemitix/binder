package net.kemitix.binder.markdown;

import lombok.Getter;

@Getter
public class MarkdownOutputException extends RuntimeException {

    private final String output;

    public MarkdownOutputException(String message, String output) {
        super(message);
        this.output = output;
    }

    public MarkdownOutputException(String message, String output, Throwable cause) {
        super(message, cause);
        this.output = output;
    }
}
