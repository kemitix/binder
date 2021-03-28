package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StrongEmphasisNodeHandler<T, R>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return StrongEmphasis.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        List<T> collect = content.collect(Collectors.toList());
        if (collect.size() != 1) {
            throw new RuntimeException("Not passed a single content item: %d sent"
                    .formatted(collect.size()));
        }
        return strongEmphasisBody(collect.get(0), context);
    }

    Stream<T> strongEmphasisBody(T content, Context<R> context);
}
