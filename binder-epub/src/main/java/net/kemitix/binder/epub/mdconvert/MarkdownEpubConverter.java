package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class MarkdownEpubConverter
        implements MarkdownConverter<String, EpubRenderHolder> {

    private final Parser parser;
    private final Instance<NodeHandler<String, EpubRenderHolder>> nodeHandlers;

    @Inject
    public MarkdownEpubConverter(
            Parser parser,
            @Epub Instance<NodeHandler<String, EpubRenderHolder>> nodeHandlers
    ) {
        this.parser = parser;
        this.nodeHandlers = nodeHandlers;
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Stream<NodeHandler<String, EpubRenderHolder>> getNodeHandlers() {
        return nodeHandlers.stream();
    }

}
