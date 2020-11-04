package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Docx
@ApplicationScoped
public class EmphasisNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;

    @Inject
    public EmphasisNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Emphasis.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        if (content.length != 1) {
            throw new RuntimeException("Not passed a single content item: %d sent"
                    .formatted(content.length));
        }
        if (content[0] instanceof R) {
            R r = (R) content[0];
            Object italic =
                    docx.italic(
                            r.getContent().toArray()
                    );
            return new Object[] {
                    italic
            };
        } else {
            throw new RuntimeException("Not passed content in an R");
        }
    }
}
