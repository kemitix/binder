package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.BulletListNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class BulletListEpubNodeHandler
        implements BulletListNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {
    @Override
    public Stream<String> bulletListBody(
            Stream<String> content,
            Context<EpubRenderHolder> context
    ) {
        return Stream.of(
                "<ul>%s</ul>"
                        .formatted(collect(content))
        );
    }
}
