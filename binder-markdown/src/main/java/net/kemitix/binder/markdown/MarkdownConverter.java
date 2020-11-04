package net.kemitix.binder.markdown;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface MarkdownConverter {

    default Object[] convert(String markdown) {
        Document document = getParser().parse(markdown);
        Object[] accepted = accept(document);
        return accepted;
    }

    Parser getParser();

    default Object[] accept(Node node) {
        NodeHandler handler = findHandler(node.getClass());
        Object[] objects = handler.handle(node, this);
        return objects;
    }

    default NodeHandler findHandler(Class<? extends Node> aClass) {
        return getNodeHandlers()
                .filter(handler -> handler.canHandle(aClass))
                .findFirst()
                .orElseGet(() -> new NodeHandler() {
                    @Override
                    public boolean canHandle(Class<? extends Node> ignoredClass) {
                        return true;
                    }

                    @Override
                    public Object[] handle(Node node, MarkdownConverter converter) {
                        throw new RuntimeException(
                                "Unhandled Markdown Type: %s".formatted(
                                        aClass.getSimpleName()));
                    }
                });
    }

    Stream<NodeHandler> getNodeHandlers();
}
