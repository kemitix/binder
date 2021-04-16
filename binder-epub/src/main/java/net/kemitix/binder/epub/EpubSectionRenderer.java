package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class EpubSectionRenderer
        implements AggregateRenderer<EpubRenderer, HtmlSection, Content, EpubRenderHolder> {

    private final Instance<EpubRenderer> epubRenderers;

    @Inject
    public EpubSectionRenderer(
            @Epub Instance<EpubRenderer> epubRenderers
    ) {
        this.epubRenderers = epubRenderers;
    }

    public Stream<Content> render(
            HtmlSection htmlSection,
            Context<EpubRenderHolder> context
    ) {
        return findRenderer(htmlSection, epubRenderers)
                .render(htmlSection, context);
    }
}
