package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface NodeHandler<T> {

    Class<? extends Node> getNodeClass();

    default boolean canHandle(Class<? extends Node> aClass) {
        return getNodeClass().equals(aClass);
    }

    default Stream<T> handle(
            Node node,
            MarkdownConverter<T> converter,
            Context context
    ) {
        Stream<T> children = handleChildren(node, converter, context);
        return Stream.concat(
                body(node, children, context),
                handleNext(node, converter, context)
        );
    }

    default Stream<T> body(Node node, Stream<T> content, Context context) {
        return content;
    }

    default Stream<T> handleChildren(
            Node node,
            MarkdownConverter<T> converter,
            Context context
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
            MarkdownConverter<T> converter,
            Context context
    ) {
        if (node.getNext() != null) {
            return converter.accept(node.getNext(), context);
        }
        return getEmpty();
    }

}
