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
            MarkdownConverter<T> converter
    ) {
        Stream<T> children = handleChildren(node, converter);
        return Stream.concat(
                body(node, children),
                handleNext(node, converter)
        );
    }

    default Stream<T> body(Node node, Stream<T> content) {
        return content;
    }

    default Stream<T> handleChildren(
            Node node,
            MarkdownConverter<T> converter
    ) {
        if (node.getFirstChild() != null) {
            return converter.accept(node.getFirstChild());
        }
        return getEmpty();
    }

    default Stream<T> getEmpty() {
        return Stream.empty();
    }

    default Stream<T> handleNext(
            Node node,
            MarkdownConverter<T> converter
    ) {
        if (node.getNext() != null) {
            return converter.accept(node.getNext());
        }
        return getEmpty();
    }

}
