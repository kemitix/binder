package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.BulletListItemNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class BulletListItemDocxNodeHandler
        implements BulletListItemNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Class<? extends Node> getNodeClass() {
        return BulletListItem.class;
    }

    @Override
    public Stream<Object> bulletListItemBody(
            String text,
            Context<DocxRenderHolder> context
    ) {
        var docx = context.getRendererHolder().getRenderer();
        return Stream.of(docx.bulletItem(text));
    }

}
