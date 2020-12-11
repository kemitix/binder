package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HtmlEntity;
import com.vladsch.flexmark.util.ast.Node;

public interface HtmlEntityNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HtmlEntity.class;
    }

}
