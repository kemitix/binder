package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HeadingEpubNodeHandler
        implements HeadingNodeHandler<String>,
        EpubNodeHandler, AlignableParagraph {

    public Stream<String> hierarchicalHeader(
            int level,
            String text,
            Context context
    ) {
        return align("h%d".formatted(level), text, context);
    }

    @Override
    public Stream<String> blankBreak() {
        return Stream.of(
                """
                <p style="text-align: center">———</p>
                """
        );
    }

    @Override
    public Stream<String> namedBreak(String name) {
        return Stream.of(
                """
                <p>\s</p>
                <p style="text-align: center; page-break-after: avoid;">
                  —\s%s\s—
                </p>
                """.formatted(name)
        );
    }

}
