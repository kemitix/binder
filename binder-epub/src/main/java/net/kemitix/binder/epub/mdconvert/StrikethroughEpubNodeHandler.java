package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.StrikethroughNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class StrikethroughEpubNodeHandler
        implements StrikethroughNodeHandler<String>, EpubNodeHandler  {
    @Override
    public Stream<String> strikethroughBody(String content) {
        return Stream.of(
                "<del>%s</del>"
                        .formatted(content)
        );
    }
}
