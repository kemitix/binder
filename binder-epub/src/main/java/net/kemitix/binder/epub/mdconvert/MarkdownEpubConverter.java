package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.markdown.NodeHandler;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class MarkdownEpubConverter
        implements MarkdownConverter<String, EpubRenderHolder> {

    private final Parser parser;
    private final Instance<NodeHandler<String, EpubRenderHolder>> nodeHandlers;
    private final Map<Class, NodeHandler<String, EpubRenderHolder>> nodeHandlerMap = new HashMap<>();

    @Inject
    public MarkdownEpubConverter(
            Parser parser,
            @Epub Instance<NodeHandler<String, EpubRenderHolder>> nodeHandlers
    ) {
        this.parser = parser;
        this.nodeHandlers = nodeHandlers;
    }

    @PostConstruct
    void init() {
        nodeHandlers.forEach(handler ->
                nodeHandlerMap.put(handler.getNodeClass(), handler));
    }

    @Override
    public Optional<NodeHandler<String, EpubRenderHolder>> lookupHandler(Class<? extends Node> aClass) {
        return Optional.ofNullable(nodeHandlerMap.get(aClass));
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Stream<NodeHandler<String, EpubRenderHolder>> getNodeHandlers() {
        return nodeHandlers.stream();
    }

}
