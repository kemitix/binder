package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
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

    public Object[] convert(String markdown) {
        Document document = parser.parse(markdown);
        Object[] accepted = accept(document);
        return accepted;
    }

    public Object[] accept(Node node) {
        NodeHandler handler = findHandler(node.getClass());
        Object[] objects = handler.handle(node, this);
        return objects;
    }

    private NodeHandler findHandler(Class<? extends Node> aClass) {
        return nodeHandlers.stream()
                .filter(handler -> handler.canHandle(aClass))
                .findFirst()
                .orElseGet(ignoreNodeHandler());
    }

    private Supplier<NodeHandler> ignoreNodeHandler() {
        return () -> (NodeHandler) aClass -> true;
    }

}
