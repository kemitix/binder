package net.kemitix.binder.epub.mdconvert;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.epub.EpubRenderer;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class StoryEpubRenderer
        implements EpubRenderer {

    private final MarkdownConverter<String> converter;
    private final FootnoteGenerator footnoteGenerator;

    @Inject
    public StoryEpubRenderer(
            @Epub MarkdownConverter<String> converter,
            FootnoteGenerator footnoteGenerator) {
        this.converter = converter;
        this.footnoteGenerator = footnoteGenerator;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.story);
    }

    @Override
    public Stream<Content> render(HtmlSection section) {
        Stream<String> contents =
                Stream.of(
                        converter.convert(
                                Context.create(section),
                                section.getMarkdown()
                        ),
                        aboutAuthor(section)
                ).flatMap(Function.identity());


        byte[] content = contents.collect(Collectors.joining())
                .getBytes(StandardCharsets.UTF_8);
        return Stream.concat(
                Stream.of(new Content(section.getHref(), content)),
                footnoteGenerator.createFootnotes(section)
        );
    }

    private Stream<String> aboutAuthor(Section section) {
        String authorBio =
                converter.convert(
                        Context.create(),
                        section.getBio()
                ).collect(Collectors.joining());
        return Stream.of(
                """
                        <p>&nbsp;</p>
                        <blockquote>
                          <p>
                            <em>&copy; %4d %s</em>
                          </p>
                        </blockquote>
                        <p>&nbsp;</p>
                        <hr/>
                        <p>&nbsp;</p>
                        <p style="text-align: center;">
                          About the Author
                        </p>
                        """
                        .formatted(
                                section.getCopyright(),
                                section.getAuthor()
                        ), authorBio);
    }

}
