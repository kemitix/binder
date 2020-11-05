package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HeadingEpubNodeHandler
        implements HeadingNodeHandler<String>, EpubNodeHandler {

    @Override
    public Stream<String> headingBody(
            int level,
            String text
    ) {
        return Stream.of(
                "<h%d>%s</h%d>".formatted(
                        level, text, level
                ));
    }

}