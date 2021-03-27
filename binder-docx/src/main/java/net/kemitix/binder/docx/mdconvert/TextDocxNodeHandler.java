package net.kemitix.binder.docx.mdconvert;

import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.TextNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Log
@Docx
@ApplicationScoped
public class TextDocxNodeHandler
        implements TextNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Stream<Object> textBody(String text, Context<DocxRenderHolder> context) {
        var docx = context.getRenderer().getDocx();
        return Stream.of(
                docx.r(
                        docx.t(text)
                )
        );
    }
}
