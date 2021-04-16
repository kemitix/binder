package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.StrikethroughNodeHandler;
import net.kemitix.binder.spi.Context;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class StrikethroughDocxNodeHandler
        implements StrikethroughNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Stream<Object> strikethroughBody(Object content, Context<DocxRenderHolder> context) {
        if (content instanceof R) {
            var docx = context.getRendererHolder().getRenderer();
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
