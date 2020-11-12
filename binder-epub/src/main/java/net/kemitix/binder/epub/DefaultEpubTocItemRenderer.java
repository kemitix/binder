package net.kemitix.binder.epub;

import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class DefaultEpubTocItemRenderer
        implements EpubTocItemRenderer {

    @Override
    public boolean canHandle(Section.Type type) {
        // anything except story
        return !Section.Type.story.equals(type);
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
