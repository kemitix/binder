package net.kemitix.binder.app.epub;

import net.kemitix.binder.app.HtmlSection;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StoryEpubTocItemRenderer
        implements EpubTocItemRenderer {

    @Override
    public boolean canHandle(String type) {
        return "story".equals(type);
    }

    @Override
    public String render(HtmlSection section) {
        return "<li><a href=\"../%s\">%s</a> by %s</li>"
                .formatted(
                        section.getHref(),
                        section.getTitle(),
                        section.getAuthor());
    }
}
