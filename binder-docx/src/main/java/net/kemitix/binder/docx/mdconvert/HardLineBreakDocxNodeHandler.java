package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacadeRunMixIn;
import net.kemitix.binder.markdown.HardLineBreakNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class HardLineBreakDocxNodeHandler
        implements HardLineBreakNodeHandler<Object> {

    @Inject DocxFacadeRunMixIn docx;

    @Override
    public Stream<Object> lineBreak() {
        return Stream.of(
                docx.textLineBreak()
        );
    }

}
