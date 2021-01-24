package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.BlockQuoteNodeHandler;
import org.docx4j.wml.P;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class BlockQuoteDocxNodeHandler
        implements BlockQuoteNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public BlockQuoteDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> blockQuoteBody(Object content) {
        if (content instanceof P) {
            P p = (P) content;
            Object blockquote =
                    docx.blockquote(p);
            return Stream.of(
                    blockquote
            );
        } else {
            throw new RuntimeException("Not passed content in an P");
        }
    }
}
