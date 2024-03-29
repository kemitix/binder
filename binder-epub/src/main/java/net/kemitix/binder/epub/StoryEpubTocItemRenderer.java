package net.kemitix.binder.epub;

import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;
import org.apache.commons.text.StringEscapeUtils;

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
    public Stream<String> render(
            HtmlSection section,
            Context<EpubRenderHolder> context
    ) {
        return Stream.of(
                """
                        <li>
                        <a href="%s">%s</a><br/>
                        <em>%s</em><br/>
                        %s - %d words
                        </li>
                        """.formatted(
                        section.getHref(),
                        StringEscapeUtils.escapeXml11(section.getTitle()),
                        section.getAuthor(),
                        section.getGenre(),
                        section.getWords()));
    }
}
