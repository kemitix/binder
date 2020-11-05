package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.footnotes.Footnote;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface FootnoteNodeHandler<T>
        extends NodeHandler<T> {

    default Class<? extends Node> getNodeClass() {
        return Footnote.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Section section) {
        Footnote footnote = (Footnote) node;
        return footnoteBody(
                footnote.getText().unescape(),
                footnote.getFootnoteBlock().getFootnote().unescape()
        );
    }

    Stream<T> footnoteBody(String oridinal, String text);

}
