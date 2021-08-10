package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.HtmlEntityNodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Docx
@ApplicationScoped
public class HtmlEntityDocxNodeHandler
        implements HtmlEntityNodeHandler<Object, DocxRenderHolder> {

}
