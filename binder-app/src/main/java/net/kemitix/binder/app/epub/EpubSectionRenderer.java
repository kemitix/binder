package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.AggregateRenderer;
import net.kemitix.binder.app.HtmlSection;

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

    @Override
    public Content render(HtmlSection htmlSection) {
        return findRenderer(htmlSection.getType(), epubRenderers)
                .render(htmlSection);
    }
}
