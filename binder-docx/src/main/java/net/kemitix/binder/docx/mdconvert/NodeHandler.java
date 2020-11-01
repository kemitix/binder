package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface NodeHandler {

    boolean canHandle(Class<? extends Node> aClass);

    default List<Object> handle(
            Node node,
            MarkdownDocxConverter converter
    ) {
        return Stream.of(
                handleNode(node, handleChildren(node, converter)),
                handleNext(node, converter)
        )
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    List<Object> handleNode(Node node, List<Object> objects);

    default List<Object> handleChildren(
            Node node,
            MarkdownDocxConverter converter
    ) {
        if (node.getFirstChild() != null) {
            return converter.accept(node.getFirstChild());
        }
        return Collections.emptyList();
    }

    default List<Object> handleNext(
            Node node,
            MarkdownDocxConverter converter
    ) {
        if (node.getNext() != null) {
            return converter.accept(node.getNext());
        }
        return Collections.emptyList();
    }

}
