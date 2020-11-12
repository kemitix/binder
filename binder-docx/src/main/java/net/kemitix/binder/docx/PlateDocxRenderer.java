package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
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
    public boolean canHandle(Section.Type type) {
        return Section.Type.plate.equals(type);
    }

    @Override
    public Stream<DocxContent> render(Section section) {
        log.info("PLATE: %s".formatted(section.getName()));
        ArrayList<Object> contents = new ArrayList<>();
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
        contents.add(docx.breakToOddPage());
        return Stream.of(new DocxContent(contents));
    }

}
