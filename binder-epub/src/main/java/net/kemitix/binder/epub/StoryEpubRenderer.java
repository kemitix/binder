package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.NoArgsConstructor;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.footnote.FootnoteAsideGenerator;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;
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
            FootnoteAsideGenerator footnoteAsideGenerator
    ) {
        super(converter, footnoteAsideGenerator);
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
        final ArrayList<String> list = new ArrayList<>();
        list.add("");
        // copyright
        list.add("> *© %4d %s*".formatted(
                requireNonZero(section.getCopyright(),
                        "Copyright missing for " + section.getTitle()),
                requireNonBlank(section.getAuthor(),
                        "Author missing for " + section.getTitle())));
        list.add("");
        // author notes and acknowledgements
        Optional.ofNullable(section.getAuthorNotes())
                .ifPresent(authorNotes ->
                        list.add("<p style=\"text-align: center;\">%s</p>".formatted(
                                authorNotes)));
        list.add("");
        // genre and word count
        list.add("> %s - %d words".formatted(
                requireGenre(section.getGenre(), "Genre missing for " + section.getTitle()),
                requireNonZero(section.getWords(),
                        "Word count missing for " + section.getTitle())));
        list.add("");
        list.add("---");
        list.add("");
        list.add("<p style=\"text-align: center;\">About the Author</p>");
        list.add("");
        // author bio
        list.add(requireNonBlank(section.getBio(),
                "Author Bio missing for " + section.getTitle()));
        return  String.join("\n", list);
    }

    private String requireGenre(Section.Genre value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value.toString();
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
