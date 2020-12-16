package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HardLineBreak;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface HardLineBreakNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HardLineBreak.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context context) {
        return lineBreak();
    }

    Stream<T> lineBreak();

}
