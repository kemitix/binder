package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.SectionRenderer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class EpubHtmlSectionRenderer {

    private final Instance<SectionRenderer<HtmlSection, Content>> htmlSectionRenderers;

    @Inject
    public EpubHtmlSectionRenderer(
            Instance<SectionRenderer<HtmlSection, Content>> htmlSectionRenderers
    ) {
        this.htmlSectionRenderers = htmlSectionRenderers;
    }

    public Content renderContent(HtmlSection htmlSection) {
        return findRenderer(htmlSection)
                .render(htmlSection);
    }

    SectionRenderer<HtmlSection, Content> findRenderer(HtmlSection htmlSection) {
        return htmlSectionRenderers.stream()
                .filter(renderer -> renderer.canHandle(htmlSection.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported Section type: %s in %s"
                                .formatted(
                                        htmlSection.getType(),
                                        htmlSection.getName())));
    }
}
