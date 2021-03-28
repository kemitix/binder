package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.RenderHolder;

public interface HtmlCommentBlockNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HtmlCommentBlock.class;
    }

}
