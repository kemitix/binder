package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.util.ast.Node;

public interface HtmlCommentBlockNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HtmlCommentBlock.class;
    }

}
