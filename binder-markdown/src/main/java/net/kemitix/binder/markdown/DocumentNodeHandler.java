package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

public interface DocumentNodeHandler extends NodeHandler {

    default boolean canHandle(Class<? extends Node> aClass) {
        return Document.class.equals(aClass);
    }

}
