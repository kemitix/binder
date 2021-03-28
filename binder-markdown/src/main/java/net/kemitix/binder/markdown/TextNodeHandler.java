package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.stream.Stream;

public interface TextNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Text.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        return textBody(node.getChars().unescape(), context);
    }

    Stream<T> textBody(String text, Context<R> context);
}
