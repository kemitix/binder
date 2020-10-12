package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Log
@ApplicationScoped
public class TocContentSectionRenderer
        extends ContentSectionRenderer<HtmlSection> {

    private final HtmlManuscript htmlManuscript;

    @Inject
    public TocContentSectionRenderer(HtmlManuscript htmlManuscript) {
        this.htmlManuscript = htmlManuscript;
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
                .forEach(section -> {
                        html.append("<li><a href=\"../%s\">%s</a></li>".formatted(
                                section.getHref(),
                                section.getTitle()));
                });
        html.append("</ul>");
        String name = htmlSection.getName();
        String href = htmlSection.getHref();
        return htmlContent(name, href, html.toString());
    }

}
