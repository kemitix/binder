package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface NodeHandler<T> {

    Class<? extends Node> getNodeClass();

    default boolean canHandle(Class<? extends Node> aClass) {
        return getNodeClass().equals(aClass);
    }

    default Stream<T> handle(
            Node node,
            MarkdownConverter<T> converter,
            Section section) {
        Stream<T> children = handleChildren(node, converter, section);
        return Stream.concat(
                body(node, children, section),
                handleNext(node, converter, section)
        );
    }

    default Stream<T> body(Node node, Stream<T> content, Section section) {
        return content;
    }

    default Stream<T> handleChildren(
            Node node,
            MarkdownConverter<T> converter,
            Section section
    ) {
        if (node.getFirstChild() != null) {
            return converter.accept(node.getFirstChild(), section);
        }
        return getEmpty();
    }

    default Stream<T> getEmpty() {
        return Stream.empty();
    }

    default Stream<T> handleNext(
            Node node,
            MarkdownConverter<T> converter,
            Section section
    ) {
        if (node.getNext() != null) {
            return converter.accept(node.getNext(), section);
        }
        return getEmpty();
    }

}
