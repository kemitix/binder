package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.ReplacedTextMapper;
import net.kemitix.binder.docx.DocxFacade;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

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
    public List<Object> handleNode(Node node, List<Object> objects) {
        Text text = (Text) node;
        return Collections.singletonList(
                docx.r(docx.t(text.getChars().unescape()))
        );
    }
}
