package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.HtmlBlockNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HtmlBlockEpubNodeHandler
        implements HtmlBlockNodeHandler<String>, EpubNodeHandler {

    @Override
    public Stream<String> body(Node node, Stream<String> content, Context context) {
        return Stream.of(node.getChars().unescape());
    }

}
