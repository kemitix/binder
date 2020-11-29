package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Node;
import lombok.Getter;

import java.util.List;

@Getter
public class MarkdownConversionException
        extends RuntimeException {

    private final Node node;
    private final List<Object> content;
    private final Context context;

    public MarkdownConversionException(
            String message,
            Node node,
            List<Object> content,
            Context context
    ) {
        super(message);
        this.node = node;
        this.content = content;
        this.context = context;
    }
}
