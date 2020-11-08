package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.stream.Stream;

@Log
@Epub
@ApplicationScoped
public class TocEpubRenderer
        implements EpubRenderer,
        AggregateRenderer<EpubTocItemRenderer, HtmlSection, String> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<EpubTocItemRenderer> tocItemRenderers;

    @Inject
    public TocEpubRenderer(
            HtmlManuscript htmlManuscript,
            Instance<EpubTocItemRenderer> tocItemRenderers
    ) {
        this.htmlManuscript = htmlManuscript;
        this.tocItemRenderers = tocItemRenderers;
    }

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public Stream<Content> render(HtmlSection htmlSection) {
        StringBuilder html = new StringBuilder("<h1>Contents</h1>");
        html.append("<ul>");
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .filter(HtmlSection::isToc)
                .flatMap(section ->
                        findRenderer(section.getType(), tocItemRenderers)
                                .render(section))
                .forEach(render -> html.append(render));
        html.append("</ul>");
        String href = htmlSection.getHref();
        return Stream.of(htmlContent(href, html.toString()));
    }

}
