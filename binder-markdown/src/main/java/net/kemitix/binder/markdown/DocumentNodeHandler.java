package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

public interface DocumentNodeHandler
        extends NodeHandler {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Document.class;
    }

}
