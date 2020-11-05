package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface DocumentNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Document.class;
    }

    @Override
    default Stream<T> body(
            Node node,
            Stream<T> content,
            Section section
    ) {
        if ("story".equals(section.getType())) {
            return documentStoryBody(section.getTitle(), section.getAuthor(), content);
        }
        return documentBody(section.getTitle(), content);
    }

    default Stream<T> documentBody(
            String title,
            Stream<T> content
    ) {
        return content;
    }

    default Stream<T> documentStoryBody(
            String title,
            String author,
            Stream<T> content
    ) {
        return content;
    }
}
