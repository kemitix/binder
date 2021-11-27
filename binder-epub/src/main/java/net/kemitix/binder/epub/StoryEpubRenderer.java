package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.NoArgsConstructor;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.footnote.FootnoteAsideGenerator;
import net.kemitix.binder.epub.mdconvert.footnote.FootnoteHtmlContentGenerator;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
@NoArgsConstructor
public class StoryEpubRenderer
        extends MarkdownEpubRenderer
        implements EpubRenderer {

    @Inject
    public StoryEpubRenderer(
            @Epub MarkdownConverter<String, EpubRenderHolder> converter,
            FootnoteHtmlContentGenerator footnoteHtmlContentGenerator,
            FootnoteAsideGenerator footnoteAsideGenerator
    ) {
        super(converter, footnoteHtmlContentGenerator, footnoteAsideGenerator);
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.story);
    }

    @Override
    public Stream<Content> render(
            HtmlSection section,
            Context<EpubRenderHolder> context
    ) {
        String markdown = section.getMarkdown() + aboutAuthor(section);
        return renderMarkdown(section, markdown);
    }

    private String aboutAuthor(Section section) {
        return  """
                        
                        > *© %4d %s*
                        
                        <p style="text-align: center;">%s</p>

                        > %s - %d words

                        ---

                        <p style="text-align: center;">
                          About the Author
                        </p>
                        
                        %s
                        """
                .formatted(
                        requireNonZero(section.getCopyright(),
                                "Copyright missing for " + section.getTitle()),
                        requireNonBlank(section.getAuthor(),
                                "Author missing for " + section.getTitle()),
                        section.getAuthorNotes(),
                        section.getGenre(),
                        requireNonZero(section.getWords(),
                                "Word count missing for " + section.getTitle()),
                        requireNonBlank(section.getBio(),
                                "Author Bio missing for " + section.getTitle())
                );
    }

    private String requireNonBlank(String value, String message) {
        requireNonZero(value.length(), message);
        return value;
    }

    private int requireNonZero(int number, String message) {
        if (number == 0) {
            throw new IllegalArgumentException(message);
        }
        return number;
    }

}
