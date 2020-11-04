package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Docx
@ApplicationScoped
public class TextNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;

    @Inject
    public TextNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Text.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        Text text = (Text) node;
        assert content.length == 0;
        R r = docx.r(
                docx.t(
                        text.getChars().unescape()
                )
        );
        return new Object[]{
                r
        };
    }
}
