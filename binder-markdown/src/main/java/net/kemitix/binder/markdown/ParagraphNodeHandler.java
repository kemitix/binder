package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;

import java.util.stream.Stream;

public interface ParagraphNodeHandler<T, R>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Paragraph.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        return paragraphBody(content, context);
    }

    Stream<T> paragraphBody(Stream<T> content, Context<R> context);
}
