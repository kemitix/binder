package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HtmlEntity;
import com.vladsch.flexmark.util.ast.Node;

public interface HtmlEntityNodeHandler<T, R>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HtmlEntity.class;
    }

}
