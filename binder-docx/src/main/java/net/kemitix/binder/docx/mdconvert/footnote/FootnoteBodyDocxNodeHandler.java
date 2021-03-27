package net.kemitix.binder.docx.mdconvert.footnote;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.footnote.FootnoteBodyNodeHandler;
import net.kemitix.binder.spi.Footnote;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class FootnoteBodyDocxNodeHandler
        implements FootnoteBodyNodeHandler<Object, DocxRenderHolder> {

    private final DocxFootnoteStore footnoteStore;

    @Inject
    public FootnoteBodyDocxNodeHandler(
            DocxFootnoteStore footnoteStore
    ) {
        this.footnoteStore = footnoteStore;
    }

    @Override
    public Stream<Object> footnoteBody(
            Footnote.Ordinal ordinal,
            Stream<Object> content,
            Context<DocxRenderHolder> context
    ) {
        var footnote = footnoteStore.get(context.getName(), ordinal);
        var placeholder = footnote.getPlaceholder();
        var pStream = content.map(P.class::cast);
        var docx = context.getRenderer().getDocx();
        docx.footnoteAddBody(placeholder, formatBody(pStream, context));
        return Stream.empty();
    }

    // insert a tab into the first run of each paragraph within the footnote body
    private Stream<P> formatBody(
            Stream<P> content,
            Context<DocxRenderHolder> context
    ) {
        var docx = context.getRenderer().getDocx();
        return content
                .peek(p -> {
                    var r = p.getContent()
                            .stream()
                            .filter(po -> po instanceof R)
                            .map(R.class::cast)
                            .findFirst()
                            .orElseThrow();
                    r.getContent().add(0, docx.tab());
                });
    }

}
