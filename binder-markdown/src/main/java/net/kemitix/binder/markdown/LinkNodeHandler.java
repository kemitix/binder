package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.stream.Stream;

public interface LinkNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Link.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        var link = (Link) node;
        return link(link, context);
    }

    Stream<T> link(Link link, Context<R> context);

}
