package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.HtmlCommentBlockNodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Docx
@ApplicationScoped
public class HtmlCommentBlockDocxNodeHandler
        implements HtmlCommentBlockNodeHandler<Object, DocxRenderHolder> {

}
