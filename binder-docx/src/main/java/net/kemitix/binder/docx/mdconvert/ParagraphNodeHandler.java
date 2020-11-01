package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ParagraphNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;

    @Inject
    public ParagraphNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public List<Object> handleNode(Node node, List<Object> objects) {
        return Collections.singletonList(
                docx.p(objects.toArray()));
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Paragraph.class.equals(aClass);
    }
}
