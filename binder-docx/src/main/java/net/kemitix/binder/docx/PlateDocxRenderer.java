package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class PlateDocxRenderer
        implements DocxRenderer {

    private final DocxImageFacade docxImage;

    @Inject
    public PlateDocxRenderer(
            DocxImageFacade docxImage
    ) {
        this.docxImage = docxImage;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.plate);
    }

    @Override
    public Stream<DocxContent> render(Section section, Context<DocxRenderHolder> context) {
        var docx = context.getRenderer().getDocx();
        //TODO: use Arrays.asList(...)
        List<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                section.getTitle(),
                                FontSize.of(512), docx)));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                section.getMarkdown(),
                                FontSize.of(240), docx)));
        contents.add(docx.textParagraph(""));
        return Stream.of(
                new DocxContent(
                        section.getName(),
                        docx.finaliseTitlePage(context, contents))
        );
    }

}
