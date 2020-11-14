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
    default Stream<T> body(Node node, Stream<T> content, Context context) {
        FootnoteBlock footnoteBlock = (FootnoteBlock) node;
        FootnoteBody<T> footnoteBody =
                FootnoteBody.create(footnoteBlock, content);
        return footnoteBlockBody(footnoteBody);
    }

    default Stream<T> footnoteBlockBody(FootnoteBody<T> footnoteBody) {
        return Stream.empty();
    }
}
