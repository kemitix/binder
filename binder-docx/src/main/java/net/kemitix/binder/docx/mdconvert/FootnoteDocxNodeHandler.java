package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.FootnoteAnchor;
import net.kemitix.binder.markdown.FootnoteNodeHandler;
import net.kemitix.binder.spi.FootnoteStore;
import org.docx4j.wml.P;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class FootnoteDocxNodeHandler
        implements FootnoteNodeHandler<Object> {

    private final DocxFacade docx;
    private final FootnoteStore<P> footnoteStore;

    @Inject
    public FootnoteDocxNodeHandler(
            DocxFacade docx,
            @Docx FootnoteStore<P> footnoteStore
    ) {
        this.docx = docx;
        this.footnoteStore = footnoteStore;
    }

    @Override
    public Class<? extends Node> getNodeClass() {
        return com.vladsch.flexmark.ext.footnotes.Footnote.class;
    }

    @Override
    public Stream<Object> footnoteBody(FootnoteAnchor footnoteAnchor) {
        String oridinal = footnoteAnchor.getOridinal();
        List<P> paras = footnoteStore.get(footnoteAnchor.getName(), oridinal);
        return Stream.of(
                docx.footnote(oridinal, paras)
        );
    }

}
