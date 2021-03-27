package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.StrikethroughNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class StrikethroughEpubNodeHandler
        implements StrikethroughNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler  {
    @Override
    public Stream<String> strikethroughBody(String content, Context<EpubRenderHolder> context) {
        return Stream.of(
                "<del>%s</del>"
                        .formatted(content)
        );
    }
}
