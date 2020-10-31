package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Node;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class ParagraphNodeHandler
        implements NodeHandler {

    @Override
    public Stream<Node> handle(Node node, List<Object> objects) {
        return Stream.of(node);
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Paragraph.class.equals(aClass);
    }
}
