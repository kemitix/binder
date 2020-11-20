package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.ParagraphNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class ParagraphEpubNodeHandler
        implements ParagraphNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> paragraphBody(
            Stream<String> content,
            Context context
    ) {
        return Stream.of(
                "<p>%s</p>\n".formatted(collect(content))
        );
    }
}
