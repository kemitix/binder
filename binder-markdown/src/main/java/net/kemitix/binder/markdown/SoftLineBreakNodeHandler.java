package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;

import java.util.stream.Stream;

public interface SoftLineBreakNodeHandler<T, R>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return SoftLineBreak.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        return softLineBreakBody(context);
    }

    Stream<T> softLineBreakBody(Context<R> context);
}
