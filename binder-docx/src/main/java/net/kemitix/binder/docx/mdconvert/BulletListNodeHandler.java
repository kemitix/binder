package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Docx
@ApplicationScoped
public class BulletListNodeHandler
        implements NodeHandler {

    @Override
    public Class<? extends Node> getNodeClass() {
        return BulletList.class;
    }

}
