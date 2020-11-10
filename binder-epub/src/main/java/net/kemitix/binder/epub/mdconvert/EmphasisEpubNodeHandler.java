package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.EmphasisNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class EmphasisEpubNodeHandler
        implements EmphasisNodeHandler<String>, EpubNodeHandler  {
    @Override
    public Stream<String> emphasisBody(String content) {
        return Stream.of(
                "<em>%s</em>"
                        .formatted(content)
        );
    }
}
