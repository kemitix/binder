package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.util.ast.Node;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HtmlCommentBlockNodeHandler
        implements NodeHandler {

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return HtmlCommentBlock.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        return new Object[0];
    }
}
