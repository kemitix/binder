package net.kemitix.binder.markdown;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.stream.Stream;

public interface MarkdownConverter<T, R extends RenderHolder<?>> {

    Parser getParser();

    Stream<NodeHandler<T, R>> getNodeHandlers();

    default Stream<T> convert(Context<R> context, String markdown) {
        Document document = fixUpDocument(getParser().parse(markdown), context);
        return accept(document, context);
    }

    default Document fixUpDocument(
            Document document,
            Context<R> context
    ) {
        return document;
    }

    default Stream<T> accept(Node node, Context<R> context) {
        NodeHandler<T, R> handler = findHandler(node.getClass());
        return handler.handle(node, this, context);
    }

    default NodeHandler<T, R> findHandler(Class<? extends Node> aClass) {
        return getNodeHandlers()
                .filter(handler -> handler.canHandle(aClass))
                .findFirst()
                .orElseGet(() -> new NodeHandler<T, R>() {
                    @Override
                    public boolean canHandle(Class<? extends Node> ignoredClass) {
                        return true;
                    }

                    @Override
                    public Class<? extends Node> getNodeClass() {
                        return null;
                    }

                    @Override
                    public Stream<T> handle(
                            Node node,
                            MarkdownConverter<T, R> converter,
                            Context<R> context
                    ) {
                        throw new RuntimeException(
                                "Unhandled Markdown Type: %s in %s (%s)".formatted(
                                        aClass.getSimpleName(),
                                        context.getTitle(),
                                        node.getChars()
                                ));
                    }
                });
    }

}
