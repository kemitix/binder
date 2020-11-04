package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BulletListItemNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;

    @Inject
    public BulletListItemNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return BulletListItem.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        BulletListItem item = (BulletListItem) node;
        String text = item.getChildChars().unescape();
        List<Object> objects = new ArrayList<>();
        objects.add(docx.bulletItem(text));
        return objects.toArray();
    }
}
