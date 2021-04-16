package net.kemitix.binder.markdown.footnote;

import com.vladsch.flexmark.ext.footnotes.Footnote;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.NodeHandler;
import net.kemitix.binder.spi.RenderHolder;

import java.util.stream.Stream;

public interface FootnoteAnchorNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    default Class<? extends Node> getNodeClass() {
        return Footnote.class;
    }

    @Override
    default Stream<T> body(
            Node node,
            Stream<T> content,
            Context<R> context
    ) {
        return footnoteAnchor(
                FootnoteAnchor.create((Footnote) node, context),
                context
                );
    }

    Stream<T> footnoteAnchor(
            FootnoteAnchor footnoteAnchor,
            Context<R> context
    );

}
