package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Docx
@ApplicationScoped
public class ParagraphNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;

    @Inject
    public ParagraphNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        return new Object[]{
                docx.p(
                        content
                )
        };
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Paragraph.class.equals(aClass);
    }
}
