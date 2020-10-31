package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Node;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface NodeHandler {
    Stream<Node> handle(Node node, List<Object> objects);

    boolean canHandle(Class<? extends Node> aClass);
}
