package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.HardLineBreakNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class HardLineBreakDocxNodeHandler
        implements HardLineBreakNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Stream<Object> lineBreak(Context<DocxRenderHolder> context) {
        var docx = context.getRendererHolder().getRenderer();
        return Stream.of(
                docx.textLineBreak()
        );
    }

}
