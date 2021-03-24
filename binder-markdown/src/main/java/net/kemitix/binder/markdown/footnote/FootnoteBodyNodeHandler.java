package net.kemitix.binder.markdown.footnote;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.NodeHandler;
import net.kemitix.binder.spi.Footnote;

import java.util.stream.Stream;

public interface FootnoteBodyNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return FootnoteBlock.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context context) {
        var footnoteBlock = (FootnoteBlock) node;
        var ordinal = Footnote.ordinal(footnoteBlock.getText().unescape());
        return footnoteBody(ordinal, content, context);
    }

    default Stream<T> footnoteBody(Footnote.Ordinal ordinal, Stream<T> content, Context context) {
        return Stream.empty();
    }
}
