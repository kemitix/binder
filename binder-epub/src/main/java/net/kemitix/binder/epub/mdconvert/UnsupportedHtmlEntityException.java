package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.spi.Context;

import java.util.List;

public class UnsupportedHtmlEntityException extends MarkdownConversionException {
    public UnsupportedHtmlEntityException(
            String entity,
            Node node,
            List<Object> content,
            Context<?> context
    ) {
        super("HTML Entity [%s] is not supported. ".formatted(entity),
                node, content, context);
    }
}
