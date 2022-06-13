package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.stream.Stream;

public interface EmphasisNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Emphasis.class;
    }

    @Override
    default Stream<T> body(
            Node node,
            Stream<T> content,
            Context<R> context
    ) {
        return content.flatMap(item -> emphasisBody(item, context));
    }

    Stream<T> emphasisBody(T content, Context<R> context);

}
