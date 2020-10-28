package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;

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
    public boolean canHandle(String type) {
        return "plate".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("PLATE: %s".formatted(htmlSection.getName()));
        ArrayList<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                htmlSection.getTitle(),
                                FontSize.of(512))));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                htmlSection.getMarkdown(),
                                FontSize.of(240))));
        contents.add(docx.textParagraph(""));
        contents.add(docx.breakToOddPage());
        return new DocxContent(contents);
    }

}
