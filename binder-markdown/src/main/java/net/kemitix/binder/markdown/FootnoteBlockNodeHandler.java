package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Node;

public interface FootnoteBlockNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return FootnoteBlock.class;
    }

}
