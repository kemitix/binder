package net.kemitix.binder.docx;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return accept(document).collect(Collectors.toList());
    }

    private Stream<Object> accept(Node node) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(findHandler(node.getClass())
                .handle(node, objects)
                .map(handledNode ->
                        Stream.of(
                                handledNode.getFirstChild(),
                                handledNode.getNext()
                        )
                                .filter(Objects::nonNull)
                                .flatMap(this::accept)).collect(Collectors.toList()));
        return objects.stream();
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
            public Stream<Node> handle(Node node, List<Object> objects) {
                log.info("Ignoring: %s".formatted(node.getClass().getSimpleName()));
                return Stream.empty();
            }
            @Override
            public boolean canHandle(Class<? extends Node> aClass) {
                return true;
            }
        };
    }

}
