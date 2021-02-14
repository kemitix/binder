package net.kemitix.binder.docx.mdconvert;

import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.TextNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Log
@Docx
@ApplicationScoped
public class TextDocxNodeHandler
        implements TextNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public TextDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> textBody(String text) {
        return Stream.of(
                docx.r(
                        docx.t(text)
                )
        );
    }
}
