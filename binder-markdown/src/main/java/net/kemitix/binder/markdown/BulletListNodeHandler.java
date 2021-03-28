package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;

import java.util.stream.Stream;

public interface BulletListNodeHandler<T, R>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return BulletList.class;
    }

    @Override
    default Stream<T> body(
            Node node,
            Stream<T> content,
            Context<R> context
    ) {
        return bulletListBody(content, context);
    }

    default Stream<T> bulletListBody(Stream<T> content, Context<R> context) {
        return content;
    }
}
