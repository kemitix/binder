package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.BlockQuoteNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class BlockQuoteEpubNodeHandler
        implements BlockQuoteNodeHandler<String>,
        EpubNodeHandler {

    @Override
    public Stream<String> blockQuoteBody(String content) {
        return Stream.of("""
                        <blockquote>
                        %s
                        </blockquote>
                        """.formatted(content)
        );
    }

}
