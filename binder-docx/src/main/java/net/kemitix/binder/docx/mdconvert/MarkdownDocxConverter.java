package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.DocumentModifier;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.markdown.NodeHandler;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Log
@Docx
@ApplicationScoped
public class MarkdownDocxConverter
        implements MarkdownConverter<Object, DocxRenderHolder> {

    private final Parser parser;
    private final Instance<NodeHandler<Object, DocxRenderHolder>> nodeHandlers;
    private final Instance<DocumentModifier> documentModifiers;
    private final Map<Class, NodeHandler<Object, DocxRenderHolder>> nodeHandlerMap = new HashMap<>();

    @Inject
    public MarkdownDocxConverter(
            Parser parser,
            @Docx Instance<NodeHandler<Object, DocxRenderHolder>> nodeHandlers,
            Instance<DocumentModifier> documentModifiers
    ) {
        this.parser = parser;
        this.nodeHandlers = nodeHandlers;
        this.documentModifiers = documentModifiers;
    }

    @PostConstruct
    void init() {
        nodeHandlers.forEach(handler ->
                nodeHandlerMap.put(handler.getNodeClass(), handler));
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Stream<NodeHandler<Object, DocxRenderHolder>> getNodeHandlers() {
        return nodeHandlers.stream();
    }

    @Override
    public Optional<NodeHandler<Object, DocxRenderHolder>> lookupHandler(Class<? extends Node> aClass) {
        return Optional.ofNullable(nodeHandlerMap.get(aClass));
    }

    @Override
    public Document fixUpDocument(
            Document document,
            Context<DocxRenderHolder> context
    ) {
        var docReference = new AtomicReference<>(document);
        documentModifiers.stream()
                .forEachOrdered(modifier ->
                        docReference.getAndUpdate(d -> modifier.apply(d, context)));
        return docReference.get();
    }
}
