package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class DocumentNodeHandler
        implements NodeHandler {

    @Override
    public List<Object> handleNode(Node node) {
        return Collections.emptyList();
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Document.class.equals(aClass);
    }
}
