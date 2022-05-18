package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.SuperscriptNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class SuperscriptEpubNodeHandler
        implements SuperscriptNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler  {
    @Override
    public Stream<String> superscriptBody(
            String content,
            Context<EpubRenderHolder> context
        ) {
        return Stream.of(
                "<sup>%s</sup>"
                        .formatted(content)
        );
    }
}
