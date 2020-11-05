package net.kemitix.binder.markdown;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface MarkdownConverter<T> {

    Parser getParser();

    Stream<NodeHandler<T>> getNodeHandlers();

    default Stream<T> convert(String markdown) {
        Document document = getParser().parse(markdown);
        Stream<T> accepted = accept(document);
        return accepted;
    }

    default Stream<T> accept(Node node) {
        NodeHandler<T> handler = findHandler(node.getClass());
        Stream<T> objects = handler.handle(node, this);
        return objects;
    }

    default NodeHandler<T> findHandler(Class<? extends Node> aClass) {
        return getNodeHandlers()
                .filter(handler -> handler.canHandle(aClass))
                .findFirst()
                .orElseGet(() -> new NodeHandler<T>() {
                    @Override
                    public boolean canHandle(Class<? extends Node> ignoredClass) {
                        return true;
                    }

                    @Override
                    public Class<? extends Node> getNodeClass() {
                        return null;
                    }

                    @Override
                    public Stream<T> handle(Node node, MarkdownConverter<T> converter) {
                        throw new RuntimeException(
                                "Unhandled Markdown Type: %s".formatted(
                                        aClass.getSimpleName()));
                    }
                });
    }
}
