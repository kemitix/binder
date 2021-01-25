package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.StrikethroughNodeHandler;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class StrikethroughDocxNodeHandler
        implements StrikethroughNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public StrikethroughDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> strikethroughBody(Object content) {
        if (content instanceof R) {
            R r = (R) content;
            Object italic =
                    docx.strikethrough(r);
            return Stream.of(
                    italic
            );
        } else {
            throw new RuntimeException("Not passed content in an R");
        }
    }
}
