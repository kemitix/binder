package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.Objects;
import java.util.stream.Stream;

public interface NodeHandler<T, R extends RenderHolder<?>> {

    Class<? extends Node> getNodeClass();

    default boolean canHandle(Class<? extends Node> aClass) {
        return getNodeClass().equals(aClass);
    }

    default Stream<T> handle(
            Node node,
            MarkdownConverter<T, R> converter,
            Context<R> context
    ) {
        Stream<T> children = handleChildren(node, converter, context);
        return Stream.concat(
                requireNonNull(body(node, children, context), "body", node),
                requireNonNull(handleNext(node, converter, context), "handleNext", node)
        );
    }

    default <O> O requireNonNull(O object, String tag, Node node) {
        if (Objects.isNull(object)) {
            throw new MarkdownOutputException("Element is null: %s [%s]".formatted(tag, node.toString()), node.toString());
        }
        return object;
    }

    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        return content;
    }

    default Stream<T> handleChildren(
            Node node,
            MarkdownConverter<T, R> converter,
            Context<R> context
    ) {
        if (node.getFirstChild() != null) {
            return converter.accept(node.getFirstChild(), context);
        }
        return getEmpty();
    }

    default Stream<T> getEmpty() {
        return Stream.empty();
    }

    default Stream<T> handleNext(
            Node node,
            MarkdownConverter<T, R> converter,
            Context<R> context
    ) {
        if (node.getNext() != null) {
            return converter.accept(node.getNext(), context);
        }
        return getEmpty();
    }

}
