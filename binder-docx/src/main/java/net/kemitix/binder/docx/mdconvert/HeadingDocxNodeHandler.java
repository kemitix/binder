package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class HeadingDocxNodeHandler
        implements HeadingNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public HeadingDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> headingBody(int level, String text) {
        return Stream.of(docx.heading(level, text));
    }

}
