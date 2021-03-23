package net.kemitix.binder.docx.mdconvert.footnote;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.markdown.footnote.FootnoteAnchor;
import net.kemitix.binder.markdown.footnote.FootnoteAnchorNodeHandler;
import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.FootnoteImpl;
import net.kemitix.binder.spi.FootnoteStoreImpl;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class FootnoteAnchorDocxNodeHandler
        implements FootnoteAnchorNodeHandler<Object> {

    private final DocxFacade docx;
    private final DocxFootnoteStore footnoteStore;

    @Inject
    public FootnoteAnchorDocxNodeHandler(
            DocxFacade docx,
            DocxFootnoteStore footnoteStore
    ) {
        this.docx = docx;
        this.footnoteStore = footnoteStore;
    }

    @Override
    public Class<? extends Node> getNodeClass() {
        return com.vladsch.flexmark.ext.footnotes.Footnote.class;
    }

    @Override
    public Stream<Object> footnoteAnchor(FootnoteAnchor footnoteAnchor) {
        var name = footnoteAnchor.getName();
        var ordinal = footnoteAnchor.getOrdinal();
        var anchor = docx.footnote(ordinal);
        var docxFootnote = DocxFootnote.createDocx(ordinal, anchor, Stream.empty());
        footnoteStore.add(name, ordinal, docxFootnote);
        return Stream.of(
                anchor
        );
    }

}
