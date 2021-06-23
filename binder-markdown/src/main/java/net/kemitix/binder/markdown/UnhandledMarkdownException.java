package net.kemitix.binder.markdown;

import net.kemitix.binder.spi.Section;

public class UnhandledMarkdownException extends RuntimeException {
    public UnhandledMarkdownException(
            String symbolName,
            Section.Name sourceTitle,
            String sourceText,
            int lineNumber
    ) {
        super("Unhandled Markdown Type: %s in %s at line %d (%s)"
                .formatted(symbolName, sourceTitle, lineNumber, sourceText));
    }
}
