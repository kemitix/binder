package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Log
@ApplicationScoped
public class MarkdownDocxConverter {

    private final Parser parser;
    private final Instance<NodeHandler> nodeHandlers;

    @Inject
    public MarkdownDocxConverter(
            Parser parser,
            Instance<NodeHandler> nodeHandlers
    ) {
        this.parser = parser;
        this.nodeHandlers = nodeHandlers;
    }

    public List<Object> convert(String markdown) {
        Document document = parser.parse(markdown);
        return accept(document);
    }

    public List<Object> accept(Node node) {
        return findHandler(node.getClass())
                .handle(node, this);
    }

    private NodeHandler findHandler(Class<? extends Node> aClass) {
        return nodeHandlers.stream()
                .filter(handler -> handler.canHandle(aClass))
                .findFirst()
                .orElseGet(ignoreNodeHandler());
    }

    private Supplier<NodeHandler> ignoreNodeHandler() {
        return () -> new NodeHandler() {
            @Override
            public List<Object> handleNode(Node node, List<Object> objects) {
                log.info("Ignoring: %s".formatted(node.getClass().getSimpleName()));
                return Collections.emptyList();
            }
            @Override
            public boolean canHandle(Class<? extends Node> aClass) {
                return true;
            }
        };
    }

}
