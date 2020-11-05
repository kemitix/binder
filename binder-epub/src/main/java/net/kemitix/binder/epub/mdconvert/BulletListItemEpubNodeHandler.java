package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.BulletListItemNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class BulletListItemEpubNodeHandler
        implements BulletListItemNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> bulletListItemBody(String text) {
        return Stream.of(
                "<li>%s</li>"
                        .formatted(text)
        );
    }
}
