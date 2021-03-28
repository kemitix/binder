package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.BulletListItemNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class BulletListItemEpubNodeHandler
        implements BulletListItemNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {
    @Override
    public Stream<String> bulletListItemBody(
            String text,
            Context<EpubRenderHolder> context
    ) {
        return Stream.of(
                "<li>%s</li>"
                        .formatted(text)
        );
    }
}
