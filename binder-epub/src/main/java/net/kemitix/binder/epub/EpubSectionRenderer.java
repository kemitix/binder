package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class EpubSectionRenderer
        implements AggregateRenderer<EpubRenderer, HtmlSection, Content> {

    private final Instance<EpubRenderer> epubRenderers;

    @Inject
    public EpubSectionRenderer(
            Instance<EpubRenderer> epubRenderers
    ) {
        this.epubRenderers = epubRenderers;
    }

    public Content render(HtmlSection htmlSection) {
        return findRenderer(htmlSection.getType(), epubRenderers)
                .render(htmlSection);
    }
}
