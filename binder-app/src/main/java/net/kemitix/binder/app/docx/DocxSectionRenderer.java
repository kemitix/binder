package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.AggregateRenderer;
import net.kemitix.binder.app.HtmlSection;

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
        var type = htmlSection.getType();
        var renderer = findRenderer(type, htmlSectionRenderers);
        return renderer.render(htmlSection);
    }

}
