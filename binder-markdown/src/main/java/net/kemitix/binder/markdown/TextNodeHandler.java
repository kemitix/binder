package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface TextNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Text.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content) {
        return textBody(node.getChars().unescape());
    }

    Stream<T> textBody(String text);
}
