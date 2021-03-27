package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.EmphasisNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class EmphasisEpubNodeHandler
        implements EmphasisNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler  {
    @Override
    public Stream<String> emphasisBody(
            String content,
            Context<EpubRenderHolder> context
        ) {
        return Stream.of(
                "<em>%s</em>"
                        .formatted(content)
        );
    }
}
