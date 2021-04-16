package net.kemitix.binder.docx.mdconvert.footnote;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.markdown.footnote.FootnoteAnchor;
import net.kemitix.binder.markdown.footnote.FootnoteAnchorNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class FootnoteAnchorDocxNodeHandler
        implements FootnoteAnchorNodeHandler<Object, DocxRenderHolder> {

    private final DocxFootnoteStore footnoteStore;

    @Inject
    public FootnoteAnchorDocxNodeHandler(
            DocxFootnoteStore footnoteStore
    ) {
        this.footnoteStore = footnoteStore;
    }

    @Override
    public Class<? extends Node> getNodeClass() {
        return com.vladsch.flexmark.ext.footnotes.Footnote.class;
    }

    @Override
    public Stream<Object> footnoteAnchor(
            FootnoteAnchor footnoteAnchor,
            Context<DocxRenderHolder> context
    ) {
        var docx = context.getRendererHolder().getRenderer();
        var name = footnoteAnchor.getName();
        var ordinal = footnoteAnchor.getOrdinal();
        var anchor = DocxFootnote.placeholder(docx.footnote(ordinal));
        var docxFootnote = DocxFootnote.createDocx(ordinal, anchor, Stream.empty());
        footnoteStore.add(name, ordinal, docxFootnote);
        return Stream.of(
                anchor.getValue()
        );
    }

}
