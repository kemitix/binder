package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Node;

public interface MarkdownConverter {
    Object[] accept(Node node);
}
