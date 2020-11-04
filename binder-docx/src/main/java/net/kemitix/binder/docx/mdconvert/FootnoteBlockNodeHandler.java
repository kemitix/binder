package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Docx
@ApplicationScoped
public class FootnoteBlockNodeHandler
        implements NodeHandler {

    @Override
    public Class<? extends Node> getNodeClass() {
        return FootnoteBlock.class;
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        return new Object[0];
    }
}
