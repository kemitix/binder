package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class StoryEpubTocItemRenderer
        implements EpubTocItemRenderer {

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.story);
    }

    @Override
    public Stream<String> render(HtmlSection section) {
        return Stream.of(
                "<li><a href=\"../%s\">%s</a> by %s</li>"
                        .formatted(
                                section.getHref(),
                                section.getTitle(),
                                section.getAuthor()));
    }
}
