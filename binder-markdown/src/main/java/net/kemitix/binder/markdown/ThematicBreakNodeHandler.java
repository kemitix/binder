package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.ThematicBreak;
import com.vladsch.flexmark.util.ast.Node;

public interface ThematicBreakNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return ThematicBreak.class;
    }

}
