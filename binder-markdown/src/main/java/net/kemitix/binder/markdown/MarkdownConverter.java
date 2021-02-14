package net.kemitix.binder.markdown;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface MarkdownConverter<T> {

    Parser getParser();

    Stream<NodeHandler<T>> getNodeHandlers();

    default Stream<T> convert(Context context, String markdown) {
        Document document = fixUpDocument(getParser().parse(markdown), context);
        Stream<T> accepted = accept(document, context);
        return accepted;
    }

    default Document fixUpDocument(Document document, Context context) {
        return document;
    }

    default Stream<T> accept(Node node, Context context) {
        NodeHandler<T> handler = findHandler(node.getClass());
        Stream<T> objects = handler.handle(node, this, context);
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
                    public Stream<T> handle(Node node, MarkdownConverter<T> converter, Context context) {
                        throw new RuntimeException(
                                "Unhandled Markdown Type: %s".formatted(
                                        aClass.getSimpleName()));
                    }
                });
    }

}
