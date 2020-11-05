package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.ParagraphNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class ParagraphDocxNodeHandler
        implements ParagraphNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public ParagraphDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> paragraphBody(Stream<Object> content) {
        return Stream.of(docx.p(content.toArray()));
    }

}
