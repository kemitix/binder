package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log
@ApplicationScoped
public class LegacyTocDocxRenderer
        implements LegacyDocxRenderer,
        AggregateRenderer<LegacyDocxTocItemRenderer, HtmlSection, Object> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<LegacyDocxTocItemRenderer> tocItemRenderers;
    private final DocxFacade docx;
    private final DocxImageFacade docxImage;

    @Inject
    public LegacyTocDocxRenderer(
            HtmlManuscript htmlManuscript,
            Instance<LegacyDocxTocItemRenderer> tocItemRenderers,
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
    public DocxContent render(HtmlSection htmlSection) {
        log.info("TOC: %s".formatted(htmlSection.getName()));
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
                .forEach(section -> content.add(
                        findRenderer(section.getType(), tocItemRenderers)
                                .render(section)));
        content.add(docx.breakToOddPage());
        return new DocxContent(content);
    }

}
