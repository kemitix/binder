package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.StrongEmphasisNodeHandler;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class StrongEmphasisDocxNodeHandler
        implements StrongEmphasisNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public StrongEmphasisDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> strongEmphasisBody(Object content) {
        if (content instanceof R) {
            R r = (R) content;
            return Stream.of(
                    docx.bold(
                            r.getContent().toArray()
                    )
            );
        } else {
            throw new RuntimeException("Not passed content in an R");
        }
    }
}
