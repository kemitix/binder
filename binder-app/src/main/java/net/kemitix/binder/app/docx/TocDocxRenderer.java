package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.AggregateRenderer;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;
import org.docx4j.wml.ObjectFactory;

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
    private final ObjectFactory factory;
    private final Instance<DocxTocItemRenderer> tocItemRenderers;
    private final DocxHelper docxHelper;

    @Inject
    public TocDocxRenderer(
            HtmlManuscript htmlManuscript,
            ObjectFactory factory,
            Instance<DocxTocItemRenderer> tocItemRenderers,
            DocxHelper docxHelper
    ) {
        this.htmlManuscript = htmlManuscript;
        this.factory = factory;
        this.tocItemRenderers = tocItemRenderers;
        this.docxHelper = docxHelper;
    }

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("TOC: %s".formatted(htmlSection.getName()));
        List<Object> content = new ArrayList<>();
        // <pic:cNvPr id="0" name="text-image-240-Contents.png"/>
        content.add(docxHelper.textParagraph("Table of Contents"));
        htmlManuscript.sections()
                .filter(HtmlSection::isDocx)
                .filter(HtmlSection::isToc)
                .forEach(section -> content.add(
                        findRenderer(section.getType(), tocItemRenderers)
                                .render(section)));
        content.add(docxHelper.breakToOddPage());
        return new DocxContent(content);
    }

}
