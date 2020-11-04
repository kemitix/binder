package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Docx
@ApplicationScoped
public class HtmlCommentBlockNodeHandler
        implements NodeHandler {

    @Override
    public Class<? extends Node> getNodeClass() {
        return HtmlCommentBlock.class;
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        return new Object[0];
    }
}
