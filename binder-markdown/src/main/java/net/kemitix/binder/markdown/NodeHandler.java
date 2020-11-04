package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Node;

import java.util.Arrays;
import java.util.stream.Stream;

public interface NodeHandler {

    Object[] EMPTY = new Object[0];

    default boolean canHandle(Class<? extends Node> aClass) {
        return getNodeClass().equals(aClass);
    }

    Class<? extends Node> getNodeClass();

    default Object[] handle(
            Node node,
            MarkdownConverter converter
    ) {
        Object[] children = handleChildren(node, converter);
        return Stream.of(
                body(node, children),
                handleNext(node, converter)
        )
                .flatMap(Arrays::stream)
                .toArray();
    }

    default Object[] body(Node node, Object[] content) {
        return content;
    }

    default Object[] handleChildren(
            Node node,
            MarkdownConverter converter
    ) {
        if (node.getFirstChild() != null) {
            return converter.accept(node.getFirstChild());
        }
        return EMPTY;
    }

    default Object[] handleNext(
            Node node,
            MarkdownConverter converter
    ) {
        if (node.getNext() != null) {
            return converter.accept(node.getNext());
        }
        return EMPTY;
    }

}
