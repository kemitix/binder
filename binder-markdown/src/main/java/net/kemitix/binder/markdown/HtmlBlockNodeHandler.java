package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HtmlBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.RenderHolder;

public interface HtmlBlockNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HtmlBlock.class;
    }

}
