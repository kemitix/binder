package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ext.footnotes.Footnote;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface FootnoteNodeHandler<T>
        extends NodeHandler<T> {

    default Class<? extends Node> getNodeClass() {
        return Footnote.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content) {
        Footnote footnote = (Footnote) node;
        return Stream.concat(
                footnoteBody(
                        footnote.getReferenceOrdinal(),
                        footnote.getFootnoteBlock().getFootnote().unescape()
                ),
                content);
    }

    Stream<T> footnoteBody(int oridinal, String text);

}
