package net.kemitix.binder.epub;

import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultEpubTocItemRenderer
        implements EpubTocItemRenderer {

    @Override
    public boolean canHandle(String type) {
        // anything except story
        return !"story".equals(type);
    }

    @Override
    public String render(HtmlSection section) {
        return "<li><a href=\"../%s\">%s</a></li>"
                .formatted(
                        section.getHref(),
                        section.getTitle());
    }
}