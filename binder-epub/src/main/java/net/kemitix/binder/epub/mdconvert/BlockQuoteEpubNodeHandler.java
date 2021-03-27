package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.BlockQuoteNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class BlockQuoteEpubNodeHandler
        implements BlockQuoteNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

    @Override
    public Stream<String> blockQuoteBody(
            String content,
            Context<EpubRenderHolder> context
    ) {
        return Stream.of("""
                        <blockquote>
                        %s
                        </blockquote>
                        """.formatted(content)
        );
    }

}
