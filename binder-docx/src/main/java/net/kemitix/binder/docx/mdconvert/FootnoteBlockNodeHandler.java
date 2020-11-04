package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FootnoteBlockNodeHandler
        implements NodeHandler {

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return FootnoteBlock.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        return new Object[0];
    }
}
