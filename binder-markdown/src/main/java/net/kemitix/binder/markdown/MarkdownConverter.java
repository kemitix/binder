package net.kemitix.binder.markdown;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.Optional;
import java.util.function.Supplier;
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

    default Stream<T> accept(Node rootNode, Context<R> context) {
        final Stream.Builder<T> builder = Stream.builder();
        Nodes.of(rootNode).getNodes().forEach(node -> {
            findHandler(node.getClass())
                    .handle(node, this, context)
                    .forEach(builder);
        });
        return builder.build();
    }

    default NodeHandler<T, R> findHandler(Class<? extends Node> aClass) {
        return lookupHandler(aClass)
                .orElseGet(unhandledMarkdownHandler(aClass));
    }

    default Optional<NodeHandler<T, R>> lookupHandler(Class<? extends Node> aClass) {
        return getNodeHandlers()
                .filter(handler -> handler.canHandle(aClass))
                .findFirst();
    }

    private Supplier<NodeHandler<T, R>> unhandledMarkdownHandler(Class<? extends Node> aClass) {
        return () -> new NodeHandler<T, R>() {
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
                throw new UnhandledMarkdownException(
                        aClass.getSimpleName(),
                        context.getName(),
                        node.getChars().unescape(),
                        node.getLineNumber()
                );
            }
        };
    }

}
