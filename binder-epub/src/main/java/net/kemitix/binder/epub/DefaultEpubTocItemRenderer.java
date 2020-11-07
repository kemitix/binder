package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class DefaultEpubTocItemRenderer
        implements EpubTocItemRenderer {

    @Override
    public boolean canHandle(String type) {
        // anything except story
        return !"story".equals(type);
    }

    @Override
    public Stream<String> render(HtmlSection section) {
        return Stream.of(
                "<li><a href=\"../%s\">%s</a></li>"
                        .formatted(
                                section.getHref(),
                                section.getTitle()));
    }
}
