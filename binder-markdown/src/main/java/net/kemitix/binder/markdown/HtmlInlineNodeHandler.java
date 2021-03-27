package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HtmlInline;
import com.vladsch.flexmark.util.ast.Node;

public interface HtmlInlineNodeHandler<T, R>
        extends NodeHandler<T, R> {
    @Override
    default Class<? extends Node> getNodeClass() {
        return HtmlInline.class;
    }
}
