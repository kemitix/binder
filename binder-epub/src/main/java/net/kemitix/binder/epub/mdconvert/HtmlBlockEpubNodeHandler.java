package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.HtmlBlockNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HtmlBlockEpubNodeHandler
        implements HtmlBlockNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

    @Override
    public Stream<String> body(
            Node node,
            Stream<String> content,
            Context<EpubRenderHolder> context
    ) {
        return Stream.of(node.getChars().unescape());
    }

}
