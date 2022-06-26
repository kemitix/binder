package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HeadingEpubNodeHandler
        implements HeadingNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler,
        AlignableParagraph {

    public Stream<String> hierarchicalHeader(
            int level,
            String text,
            Context<EpubRenderHolder> context
    ) {
        return align("h%d".formatted(level), text, context);
    }

    @Override
    public Stream<String> blankBreak(Context<EpubRenderHolder> context) {
        return Stream.of(
                """
                <p style="text-align: center">&mdash;&mdash;&mdash;</p>
                """
        );
    }

    @Override
    public Stream<String> namedBreak(
            String name,
            Context<EpubRenderHolder> context
    ) {
        return Stream.of(
                """
                <p>\s</p>
                <p style="text-align: center; page-break-after: avoid;">
                  &mdash;&nbsp;%s&nbsp;&mdash;
                </p>
                """.formatted(name)
        );
    }

}
