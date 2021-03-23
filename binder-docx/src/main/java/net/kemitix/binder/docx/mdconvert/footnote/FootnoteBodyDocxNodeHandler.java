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
    public Stream<Object> footnoteBody(Footnote.Ordinal ordinal, Stream<Object> content, Context context) {
        Footnote<P, R> footnote = footnoteStore.get(context.getName(), ordinal);
        R placeholder = footnote.getPlaceholder();
        docx.footnoteAddBody(placeholder, formatBody(content));
        return Stream.empty();
    }

    // insert a tab into the first run of each paragraph within the footnote body
    private Stream<Object> formatBody(Stream<Object> content) {
        return content.peek(o -> {
            if (o instanceof P) {
                var p = (P) o;
                var r = p.getContent()
                        .stream()
                        .filter(po -> po instanceof R)
                        .map(R.class::cast)
                        .findFirst()
                        .orElseThrow();
                r.getContent().add(0, docx.tab());
            }
        });
    }

}
