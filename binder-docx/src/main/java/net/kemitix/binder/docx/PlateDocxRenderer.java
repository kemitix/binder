package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.Context;
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

    private final DocxFacade docx;
    private final DocxImageFacade docxImage;

    @Inject
    public PlateDocxRenderer(
            DocxFacade docx,
            DocxImageFacade docxImage
    ) {
        this.docx = docx;
        this.docxImage = docxImage;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.plate);
    }

    @Override
    public Stream<DocxContent> render(Section section) {
        //TODO: use Arrays.asList(...)
        List<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                section.getTitle(),
                                FontSize.of(512))));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                section.getMarkdown(),
                                FontSize.of(240))));
        contents.add(docx.textParagraph(""));
        Context context = Context.create(section);
        return Stream.of(
                new DocxContent(
                        section.getName(),
                        docx.finaliseTitlePage(context, contents))
        );
    }

}
