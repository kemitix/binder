package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface DocumentNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Document.class;
    }

    @Override
    default Stream<T> body(
            Node node,
            Stream<T> content,
            Context<R> context
    ) {
        String title = context.getTitle();
        if (context.isType(Section.Type.story)) {
            return documentStoryBody(title, context.getAuthor(), content);
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
