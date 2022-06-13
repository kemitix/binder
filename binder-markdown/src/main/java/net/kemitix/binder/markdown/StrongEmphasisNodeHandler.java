package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StrongEmphasisNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return StrongEmphasis.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        return content.flatMap(item -> strongEmphasisBody(item, context));
    }

    Stream<T> strongEmphasisBody(T content, Context<R> context);
}
