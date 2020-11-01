package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

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
                .orElseGet(() -> new NodeHandler() {
                    @Override
                    public boolean canHandle(Class<? extends Node> ignoredClass) {
                        return true;
                    }

                    @Override
                    public Object[] handle(Node node, MarkdownDocxConverter converter) {
                        log.info("Unhandled type: %s".formatted(
                                aClass.getSimpleName()));
                        log.info(node.toAstString(true));
                        return new Object[0];
                    }
                });
    }

}
