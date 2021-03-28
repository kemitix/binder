package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.BlockQuoteNodeHandler;
import net.kemitix.binder.spi.Context;
import org.docx4j.wml.P;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class BlockQuoteDocxNodeHandler
        implements BlockQuoteNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Stream<Object> blockQuoteBody(
            Object content,
            Context<DocxRenderHolder> context
    ) {
        if (content instanceof P) {
            P p = (P) content;
            var docx = context.getRendererHolder().getRenderer();
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
