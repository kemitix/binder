package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DocumentNodeHandler
        implements NodeHandler {

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Document.class.equals(aClass);
    }

}
