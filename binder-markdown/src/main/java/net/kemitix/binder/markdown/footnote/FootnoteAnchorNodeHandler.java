package net.kemitix.binder.markdown.footnote;

import com.vladsch.flexmark.ext.footnotes.Footnote;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.NodeHandler;

import java.util.stream.Stream;

public interface FootnoteAnchorNodeHandler<T>
        extends NodeHandler<T> {

    default Class<? extends Node> getNodeClass() {
        return Footnote.class;
    }

    @Override
    default Stream<T> body(
            Node node,
            Stream<T> content,
            Context context
    ) {
        return footnoteAnchor(FootnoteAnchor.create((Footnote) node, context));
    }

    Stream<T> footnoteAnchor(FootnoteAnchor footnoteAnchor);

}
