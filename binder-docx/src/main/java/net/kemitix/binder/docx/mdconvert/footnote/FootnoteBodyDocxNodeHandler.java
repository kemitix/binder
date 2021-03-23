package net.kemitix.binder.docx.mdconvert.footnote;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.markdown.Context;
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
        implements FootnoteBodyNodeHandler<Object> {

    private final DocxFacade docx;
    private final DocxFootnoteStore footnoteStore;

    @Inject
    public FootnoteBodyDocxNodeHandler(
            DocxFacade docx,
            DocxFootnoteStore footnoteStore
    ) {
        this.docx = docx;
        this.footnoteStore = footnoteStore;
    }

    @Override
    public Stream<Object> footnoteBody(
            Footnote.Ordinal ordinal,
            Stream<Object> content,
            Context context
    ) {
        var footnote = footnoteStore.get(context.getName(), ordinal);
        var placeholder = footnote.getPlaceholder();
        var pStream = content.map(P.class::cast);
        docx.footnoteAddBody(placeholder, formatBody(pStream));
        return Stream.empty();
    }

    // insert a tab into the first run of each paragraph within the footnote body
    private Stream<P> formatBody(Stream<P> content) {
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
