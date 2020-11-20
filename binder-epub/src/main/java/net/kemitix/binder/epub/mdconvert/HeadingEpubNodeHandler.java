package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HeadingEpubNodeHandler
        implements HeadingNodeHandler<String>, EpubNodeHandler {

    public Stream<String> hierarchicalHeader(int level, String text) {
        return Stream.of(
                "<h%d>%s</h%d>".formatted(
                level, text, level
        ));
    }

    @Override
    public Stream<String> blankBreak() {
        return Stream.of(
                """
                <p style="text-align: center">&mdash;*&mdash;</p>
                """
        );
    }

    @Override
    public Stream<String> namedBreak(String name) {
        return Stream.of(
                """
                <p>&nbsp;</p>
                <p style="text-align: center; page-break-after: avoid;">
                  &mdash;&nbsp;%s&nbsp;&mdash;
                </p>
                """.formatted(name)
        );
    }

}
