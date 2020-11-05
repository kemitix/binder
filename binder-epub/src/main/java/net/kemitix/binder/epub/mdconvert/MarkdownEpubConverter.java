package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.stream.Stream;

@EPub
@ApplicationScoped
public class MarkdownEpubConverter
        implements MarkdownConverter<String> {

    private final Parser parser;
    private final Instance<NodeHandler<String>> nodeHandlers;

    @Inject
    public MarkdownEpubConverter(
            Parser parser,
            @EPub Instance<NodeHandler<String>> nodeHandlers
    ) {
        this.parser = parser;
        this.nodeHandlers = nodeHandlers;
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Stream<NodeHandler<String>> getNodeHandlers() {
        return nodeHandlers.stream();
    }

}
