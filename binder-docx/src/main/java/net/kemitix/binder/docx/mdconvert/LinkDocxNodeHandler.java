package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Link;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.LinkNodeHandler;
import net.kemitix.binder.spi.Context;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class LinkDocxNodeHandler
        implements LinkNodeHandler<Object, DocxRenderHolder> {

    @Override
    public Stream<Object> link(Link link, Context<DocxRenderHolder> context) {
        var docx = context.getRendererHolder().getRenderer();
        var text = link.getText().toString();
        R r = docx.r(docx.t(text));
        return Stream.of(r);
    }

}
