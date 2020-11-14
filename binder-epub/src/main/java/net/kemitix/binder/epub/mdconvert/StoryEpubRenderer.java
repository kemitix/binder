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
import java.util.List;
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
        String contents =
                converter.convert(
                        Context.create(section),
                        section.getMarkdown()
                ).collect(Collectors.joining());

        //TODO add previously published section if required
        //TODO add about the Author sections
        //contents.addAll(//TODO);

        byte[] content = contents.getBytes(StandardCharsets.UTF_8);
        return Stream.concat(
                createContent(section, content),
                footnoteGenerator.createFootnotes(section)
        );
    }

    private Stream<Content> createContent(HtmlSection source, byte[] content) {
        return Stream.of(new Content(source.getHref(), content));
    }

}
