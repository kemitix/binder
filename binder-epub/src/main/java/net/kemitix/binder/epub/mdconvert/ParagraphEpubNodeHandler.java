package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.ParagraphNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class ParagraphEpubNodeHandler
        implements ParagraphNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler,
        AlignableParagraph {
    @Override
    public Stream<String> paragraphBody(
            Stream<String> content,
            Context<EpubRenderHolder> context
    ) {
        return align("p", "%s\n".formatted(collect(content)), context);
    }
}
