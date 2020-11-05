package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.BulletListNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class BulletListEpubNodeHandler
        implements BulletListNodeHandler<String> {
    @Override
    public Stream<String> bulletListBody(Stream<String> content) {
        return Stream.of(
                "<ul>%s</ul>"
                        .formatted(content)
        );
    }
}
