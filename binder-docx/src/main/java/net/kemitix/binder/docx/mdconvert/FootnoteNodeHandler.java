package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ext.footnotes.Footnote;
import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Docx
@ApplicationScoped
public class FootnoteNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;


    @Inject
    public FootnoteNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Footnote.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        Footnote footnote = (Footnote) node;
        String id = footnote.getText().unescape();
        FootnoteBlock footnoteBlock = footnote.getFootnoteBlock();
        String footnoteBody = footnoteBlock.getFootnote().unescape();
        return new Object[] {
                docx.footnote(id, footnoteBody)
        };
    };
}
