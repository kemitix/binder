package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface ParagraphNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Paragraph.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Section section) {
        return paragraphBody(content);
    }

    Stream<T> paragraphBody(Stream<T> content);
}
