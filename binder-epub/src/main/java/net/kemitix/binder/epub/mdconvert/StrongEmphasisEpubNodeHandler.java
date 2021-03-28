package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.StrongEmphasisNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class StrongEmphasisEpubNodeHandler
        implements StrongEmphasisNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {
    @Override
    public Stream<String> strongEmphasisBody(String content, Context<EpubRenderHolder> context) {
        return Stream.of(
                "<strong>%s</strong>"
                        .formatted(content)
        );
    }
}
