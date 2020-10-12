package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.AggregateRenderer;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Log
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
    public Content render(HtmlSection htmlSection) {
        StringBuilder html = new StringBuilder("<h1>Table of Contents</h1>");
        html.append("<ul>");
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .filter(HtmlSection::isToc)
                .forEach(section -> html.append(
                        findRenderer(section.getType(), tocItemRenderers)
                                .render(section)));
        html.append("</ul>");
        String href = htmlSection.getHref();
        return htmlContent(href, html.toString());
    }

}
