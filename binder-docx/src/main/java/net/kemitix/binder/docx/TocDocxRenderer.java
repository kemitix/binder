package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log
@ApplicationScoped
public class TocDocxRenderer
        implements DocxRenderer,
        AggregateRenderer<DocxTocItemRenderer, Section, Object> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<DocxTocItemRenderer> tocItemRenderers;
    private final DocxFacade docx;
    private final DocxImageFacade docxImage;

    @Inject
    public TocDocxRenderer(
            HtmlManuscript htmlManuscript,
            Instance<DocxTocItemRenderer> tocItemRenderers,
            DocxFacade docx,
            DocxImageFacade docxImage
    ) {
        this.htmlManuscript = htmlManuscript;
        this.tocItemRenderers = tocItemRenderers;
        this.docx = docx;
        this.docxImage = docxImage;
    }

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public DocxContent render(Section section) {
        log.info("TOC: %s".formatted(section.getName()));
        List<Object> content = new ArrayList<>();
        content.add(docx.textParagraph(""));
        content.add(
                docx.drawings(
                        docxImage.textImages(
                                "Contents",
                                FontSize.of(240))));
        content.add(docx.textParagraph(""));
        htmlManuscript.sections()
                .filter(HtmlSection::isDocx)
                .filter(HtmlSection::isToc)
                .forEach(s -> content.add(
                        findRenderer(s.getType(), tocItemRenderers)
                                .render(s)));
        content.add(docx.breakToOddPage());
        return new DocxContent(content);
    }

}
