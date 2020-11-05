package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface FootnoteBlockNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return FootnoteBlock.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Section section) {
        return footnoteBlockBody(
                ((FootnoteBlock) node),
                content);
    }

    default Stream<T> footnoteBlockBody(
            FootnoteBlock footnoteBlock,
            Stream<T> content
    ) {
        return Stream.empty();
    }
}
