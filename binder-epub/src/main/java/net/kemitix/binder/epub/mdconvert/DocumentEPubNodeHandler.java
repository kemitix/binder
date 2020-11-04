package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@EPub
@ApplicationScoped
public class DocumentEPubNodeHandler
        implements NodeHandler {

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Document.class.equals(aClass);
    }

}
