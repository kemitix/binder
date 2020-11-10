package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.BulletListItemNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class BulletListItemDocxNodeHandler
        implements BulletListItemNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public BulletListItemDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Class<? extends Node> getNodeClass() {
        return BulletListItem.class;
    }

    @Override
    public Stream<Object> bulletListItemBody(String text) {
        return Stream.of(docx.bulletItem(text));
    }

}
