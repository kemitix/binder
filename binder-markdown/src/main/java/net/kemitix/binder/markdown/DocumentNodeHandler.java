package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface DocumentNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Document.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content) {
        return documentBody((Document) node, content);
    }

    default Stream<T> documentBody(Document node, Stream<T> content) {
        return content;
    }
}
