package net.kemitix.binder.docx;

import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DocxSectionRenderer
        implements AggregateRenderer<DocxRenderer, HtmlSection, DocxContent> {

    private final Instance<DocxRenderer> htmlSectionRenderers;

    @Inject
    public DocxSectionRenderer(Instance<DocxRenderer> htmlSectionRenderers) {
        this.htmlSectionRenderers = htmlSectionRenderers;
    }

    public DocxContent render(HtmlSection htmlSection) {
        return findRenderer(htmlSection.getType(), htmlSectionRenderers)
                .render(htmlSection);
    }

}
