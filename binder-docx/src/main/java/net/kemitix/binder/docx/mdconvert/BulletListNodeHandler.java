package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.util.ast.Node;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulletListNodeHandler
        implements NodeHandler {

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return BulletList.class.equals(aClass);
    }

}
