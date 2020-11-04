package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Docx
@ApplicationScoped
public class SoftLineBreakNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;

    @Inject
    public SoftLineBreakNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Class<? extends Node> getNodeClass() {
        return SoftLineBreak.class;
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        return new Object[]{
                docx.r(
                        docx.t(
                                " "
                        )
                )
        };
    }
}
