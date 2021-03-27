package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.ParagraphNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class ParagraphDocxNodeHandler
        implements ParagraphNodeHandler<Object, DocxRenderHolder>,
        AlignableParagraph {

    @Override
    public Stream<Object> paragraphBody(
            Stream<Object> content,
            Context<DocxRenderHolder> context
    ) {
        var docx = context.getRenderer().getDocx();
        return alignP(
                docx.styledP(
                        context.getParaStyleName(),
                        docx.p(content.toArray())),
                docx,
                context);
    }

}
