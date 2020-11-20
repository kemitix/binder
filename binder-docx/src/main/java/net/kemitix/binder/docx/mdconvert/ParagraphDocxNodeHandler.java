package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.ParagraphNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class ParagraphDocxNodeHandler
        implements ParagraphNodeHandler<Object> {

    private final DocxFacade docx;

    @Inject
    public ParagraphDocxNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public Stream<Object> paragraphBody(Stream<Object> content, Context context) {
        //TODO: check if setting alignment is required
//        for para in paras:
//        if para.paragraph_format.alignment is None:
//        para.paragraph_format.alignment = WD_PARAGRAPH_ALIGNMENT.JUSTIFY
        return Stream.of(docx.p(content.toArray()));
    }

}
