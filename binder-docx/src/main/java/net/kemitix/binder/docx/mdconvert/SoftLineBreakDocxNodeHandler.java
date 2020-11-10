package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;
import net.kemitix.binder.markdown.SoftLineBreakNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class SoftLineBreakDocxNodeHandler
        implements SoftLineBreakNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public SoftLineBreakDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> softLineBreakBody() {
        return Stream.of(
                docx.r(
                        docx.t(
                                " "
                        )
                )
        );
    }
}
