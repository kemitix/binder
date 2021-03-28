package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.ThematicBreak;
import com.vladsch.flexmark.util.ast.Node;

public interface ThematicBreakNodeHandler<T, R>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return ThematicBreak.class;
    }

}
