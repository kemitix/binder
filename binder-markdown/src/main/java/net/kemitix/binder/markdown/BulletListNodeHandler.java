package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.util.ast.Node;

public interface BulletListNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return BulletList.class;
    }

}
