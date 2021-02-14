package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.footnote.FootnoteHtmlContentGenerator;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class StoryEpubRenderer
        implements EpubRenderer {

    private final MarkdownConverter<String> converter;
    private final FootnoteHtmlContentGenerator footnoteHtmlContentGenerator;

    @Inject
    public StoryEpubRenderer(
            @Epub MarkdownConverter<String> converter,
            FootnoteHtmlContentGenerator footnoteHtmlContentGenerator) {
        this.converter = converter;
        this.footnoteHtmlContentGenerator = footnoteHtmlContentGenerator;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.story);
    }

    @Override
    public Stream<Content> render(HtmlSection section) {
        String markdown = section.getMarkdown() + aboutAuthor(section);
        List<String> convertedBody = converter.convert(
                Context.create(section),
                markdown
        ).collect(Collectors.toList());
        Stream<String> contents =
                Stream.of(
                        convertedBody.stream()
                ).flatMap(Function.identity());

        byte[] content = contents.collect(Collectors.joining())
                .getBytes(StandardCharsets.UTF_8);
        return Stream.concat(
                Stream.of(new Content(section.getHref(), content)),
                footnoteHtmlContentGenerator.createFootnotes(section)
        );
    }

    private String aboutAuthor(Section section) {
        return  """
                        
                        > *© %4d %s*

                        ---

                        <p style="text-align: center;">
                          About the Author
                        </p>
                        
                        %s
                        """
                .formatted(
                        section.getCopyright(),
                        section.getAuthor(),
                        section.getBio()
                );
    }

}
