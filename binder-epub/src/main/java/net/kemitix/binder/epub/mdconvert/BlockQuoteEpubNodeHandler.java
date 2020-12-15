package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.BlockQuoteNodeHandler;
import net.kemitix.binder.markdown.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class BlockQuoteEpubNodeHandler
        implements BlockQuoteNodeHandler<String>,
        EpubNodeHandler {

    @Override
    public Stream<String> body(Node node, Stream<String> content, Context context) {
        return Stream.of(
                """
                        <blockquote>
                        %s
                        </blockquote>
                        """.formatted(collect(content))
        );
    }

}
