package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.Objects;
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
        String title = Objects.requireNonNullElse(section.getTitle(), "");
        if ("story".equals(section.getType())) {
            String author = Objects.requireNonNull(section.getAuthor(), "No author for " + section.getName());
            return documentStoryBody(title, author, content);
        }
        return documentBody(title, content);
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
