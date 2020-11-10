package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface SoftLineBreakNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return SoftLineBreak.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Section section) {
        return softLineBreakBody();
    }

    Stream<T> softLineBreakBody();
}
