package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.StrongEmphasisNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class StrongEmphasisEpubNodeHandler
        implements StrongEmphasisNodeHandler<String>  {
    @Override
    public Stream<String> strongEmphasisBody(String content) {
        return Stream.of(
                "<strong>%s</strong>"
                        .formatted(content)
        );
    }
}
