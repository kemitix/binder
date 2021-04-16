package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class HeadingDocxNodeHandler
        implements HeadingNodeHandler<Object, DocxRenderHolder>,
        AlignableParagraph {

    @Override
    public Stream<Object> hierarchicalHeader(
            int level,
            String text,
            Context<DocxRenderHolder> context
    ) {
        var docx = context.getRendererHolder().getRenderer();
        if (text.isBlank()) {
            return Stream.of(
                    docx.styledP(
                            context.getParaStyleName(),
                            docx.textParagraph(""))
            );
        }
        return alignP(
                docx.heading(level, text),
                docx,
                context);
    }

    @Override
    public Stream<Object> blankBreak(Context<DocxRenderHolder> context) {
        var docx = context.getRendererHolder().getRenderer();
        return Stream.of(
                docx.textParagraphCentered(EM_DASH + EM_DASH + EM_DASH)
        );
    }

    private static final String EM_DASH = "\u2014";
    private static final String NO_BREAK_SPACE = "\u00A0";

    @Override
    public Stream<Object> namedBreak(
            String name,
            Context<DocxRenderHolder> context
    ) {
        var docx = context.getRendererHolder().getRenderer();
        return Stream.of(
                docx.p(),
                docx.keepWithNext(
                        docx.textParagraphCentered(
                                String.join("", Arrays.asList(
                                        EM_DASH,
                                        NO_BREAK_SPACE,
                                        name,
                                        NO_BREAK_SPACE,
                                        EM_DASH
                                )))));
    }

}
