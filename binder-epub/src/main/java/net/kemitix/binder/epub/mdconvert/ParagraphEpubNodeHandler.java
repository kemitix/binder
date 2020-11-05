package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.ParagraphNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class ParagraphEpubNodeHandler
        implements ParagraphNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> paragraphBody(Stream<String> content) {
        return Stream.of(
                "<p>%s</p>".formatted(collect(content))
        );
    }
}
