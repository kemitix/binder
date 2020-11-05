package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@EPub
@ApplicationScoped
public class HeadingEpubNodeHandler
        implements HeadingNodeHandler<String> {

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
