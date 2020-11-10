package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.FootnoteAnchor;
import net.kemitix.binder.markdown.FootnoteNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class FootnoteDocxNodeHandler
        implements FootnoteNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public FootnoteDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Class<? extends Node> getNodeClass() {
        return com.vladsch.flexmark.ext.footnotes.Footnote.class;
    }

    @Override
    public Stream<Object> footnoteBody(FootnoteAnchor footnoteAnchor) {
        String oridinal = footnoteAnchor.getOridinal();
        String text = "footnoteAnchor.getText()";
        return Stream.of(
                docx.footnote(oridinal, text)
        );
    }

}
