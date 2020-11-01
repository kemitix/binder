package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Node;

import java.util.Arrays;
import java.util.stream.Stream;

public interface NodeHandler {

    Object[] EMPTY = new Object[0];

    boolean canHandle(Class<? extends Node> aClass);

    default Object[] handle(
            Node node,
            MarkdownDocxConverter converter
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
            MarkdownDocxConverter converter
    ) {
        if (node.getFirstChild() != null) {
            return converter.accept(node.getFirstChild());
        }
        return EMPTY;
    }

    default Object[] handleNext(
            Node node,
            MarkdownDocxConverter converter
    ) {
        if (node.getNext() != null) {
            return converter.accept(node.getNext());
        }
        return EMPTY;
    }

}
