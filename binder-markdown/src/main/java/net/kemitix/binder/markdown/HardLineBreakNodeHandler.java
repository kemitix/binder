package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HardLineBreak;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.stream.Stream;

public interface HardLineBreakNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HardLineBreak.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        return lineBreak(context);
    }

    Stream<T> lineBreak(Context<R> context);

}
