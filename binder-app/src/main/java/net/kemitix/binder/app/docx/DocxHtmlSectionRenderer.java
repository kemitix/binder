package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.SectionRenderer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DocxHtmlSectionRenderer {

    private final Instance<DocxSectionRenderer> htmlSectionRenderers;

    @Inject
    public DocxHtmlSectionRenderer(Instance<DocxSectionRenderer> htmlSectionRenderers) {
        this.htmlSectionRenderers = htmlSectionRenderers;
    }

    public DocxContent renderContent(HtmlSection htmlSection) {
        return findRenderer(htmlSection)
                .render(htmlSection);
    }

    private SectionRenderer<HtmlSection, DocxContent> findRenderer(HtmlSection htmlSection) {
        return htmlSectionRenderers.stream()
                .filter(renderer -> renderer.canHandle(htmlSection.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported docx section type: %s in %s"
                                .formatted(
                                        htmlSection.getType(),
                                        htmlSection.getName())));
    }
}
