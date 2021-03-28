package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.DocumentModifier;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
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

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Stream<NodeHandler<Object, DocxRenderHolder>> getNodeHandlers() {
        return nodeHandlers.stream();
    }

    @Override
    public Document fixUpDocument(
            Document document,
            Context context
    ) {
        var docReference = new AtomicReference<>(document);
        documentModifiers.stream()
                .forEachOrdered(modifier ->
                        docReference.getAndUpdate(d -> modifier.apply(d, context)));
        return docReference.get();
    }
}
