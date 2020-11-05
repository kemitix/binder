package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.stream.Stream;

@Log
@Docx
@ApplicationScoped
public class MarkdownDocxConverter
        implements MarkdownConverter<Object> {

    private final Parser parser;
    private final Instance<NodeHandler<Object>> nodeHandlers;

    @Inject
    public MarkdownDocxConverter(
            Parser parser,
            @Docx Instance<NodeHandler<Object>> nodeHandlers
    ) {
        this.parser = parser;
        this.nodeHandlers = nodeHandlers;
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Stream<NodeHandler<Object>> getNodeHandlers() {
        return nodeHandlers.stream();
    }

}
