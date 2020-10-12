package net.kemitix.binder.app.epub;

import net.kemitix.binder.app.HtmlSection;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultEpubTocRenderer
        implements EpubTocRenderer {

    @Override
    public boolean canHandle(String type) {
        return true;
    }

    @Override
    public String render(HtmlSection section) {
        return "<li><a href=\"../%s\">%s</a></li>"
                .formatted(
                        section.getHref(),
                        section.getTitle());
    }
}
