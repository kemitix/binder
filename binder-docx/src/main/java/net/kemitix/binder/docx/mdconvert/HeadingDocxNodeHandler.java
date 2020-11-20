package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.HeadingNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class HeadingDocxNodeHandler
        implements HeadingNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public HeadingDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> hierarchicalHeader(int level, String text) {
        return Stream.of(docx.heading(level, text));
    }

    @Override
    public Stream<Object> blankBreak() {
        return Stream.of(
                docx.textParagraphCentered("—*—")
        );
    }

    private static final String EM_DASH = "\u2014";
    private static final String NO_BREAK_SPACE = "\u00A0";

    @Override
    public Stream<Object> namedBreak(String name) {
        return Stream.of(
                docx.p(),
                docx.keepWithNext(
                        docx.textParagraphCentered(
                                String.join("", Arrays.asList(
                                        EM_DASH,
                                        NO_BREAK_SPACE,
                                        name,
                                        NO_BREAK_SPACE,
                                        EM_DASH
                                )))));
    }

}
