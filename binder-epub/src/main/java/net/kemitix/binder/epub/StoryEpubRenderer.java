package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.NoArgsConstructor;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.footnote.FootnoteAsideContentGenerator;
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
            FootnoteAsideContentGenerator footnoteAsideContentGenerator
    ) {
        super(converter, footnoteHtmlContentGenerator, footnoteAsideContentGenerator);
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
