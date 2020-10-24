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
public class TocDocxRenderer
        implements DocxRenderer,
        AggregateRenderer<DocxTocItemRenderer, HtmlSection, Object> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<DocxTocItemRenderer> tocItemRenderers;
    private final DocxFacade docx;

    @Inject
    public TocDocxRenderer(
            HtmlManuscript htmlManuscript,
            Instance<DocxTocItemRenderer> tocItemRenderers,
            DocxFacade docx
    ) {
        this.htmlManuscript = htmlManuscript;
        this.tocItemRenderers = tocItemRenderers;
        this.docx = docx;
    }

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("TOC: %s".formatted(htmlSection.getName()));
        List<Object> content = new ArrayList<>();
        int pageWidth = 5000;//TODO
        content.add(docx.textImage("Table of Contents", FontSize.of(240), pageWidth));
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
