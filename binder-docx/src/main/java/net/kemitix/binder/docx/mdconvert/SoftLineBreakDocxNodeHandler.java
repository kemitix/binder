package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.SoftLineBreakNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class SoftLineBreakDocxNodeHandler
        implements SoftLineBreakNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Stream<Object> softLineBreakBody(Context<DocxRenderHolder> context) {
        var docx = context.getRendererHolder().getRenderer();
        return Stream.of(
                docx.r(
                        docx.t(
                                " "
                        )
                )
        );
    }
}
