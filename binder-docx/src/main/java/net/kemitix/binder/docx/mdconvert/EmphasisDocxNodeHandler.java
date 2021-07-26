package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.EmphasisNodeHandler;
import net.kemitix.binder.spi.Context;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class EmphasisDocxNodeHandler
        implements EmphasisNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Stream<Object> emphasisBody(
            Object content,
            Context<DocxRenderHolder> context
    ) {
        if (content instanceof R) {
            var docx = context.getRendererHolder().getRenderer();
            R r = (R) content;
            Object italic =
                    docx.italic(r);
            return Stream.of(
                    italic
            );
        } else {
            throw new RuntimeException("Not passed content in an R");
        }
    }
}
