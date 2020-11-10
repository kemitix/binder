package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface BulletListNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return BulletList.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Section section) {
        return bulletListBody(content);
    }

    default Stream<T> bulletListBody(Stream<T> content) {
        return content;
    }
}
