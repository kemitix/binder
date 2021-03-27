package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.NoArgsConstructor;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.footnote.FootnoteHtmlContentGenerator;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Epub
@ApplicationScoped
@NoArgsConstructor
public class MarkdownEpubRenderer
        implements EpubRenderer {

    private MarkdownConverter<String, EpubRenderHolder> converter;
    private FootnoteHtmlContentGenerator footnoteHtmlContentGenerator;

    @Inject
    public MarkdownEpubRenderer(
            @Epub MarkdownConverter<String, EpubRenderHolder> converter,
            FootnoteHtmlContentGenerator footnoteHtmlContentGenerator) {
        this.converter = converter;
        this.footnoteHtmlContentGenerator = footnoteHtmlContentGenerator;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.markdown);
    }

    @Override
    public Stream<Content> render(
            HtmlSection section,
            Context<EpubRenderHolder> context
    ) {
        String markdown = section.getMarkdown();
        return renderMarkdown(section, markdown);
    }

    protected Stream<Content> renderMarkdown(
            HtmlSection section,
            String markdown
    ) {
        EpubRenderHolder epubRenderHolder = new EpubRenderHolder() {};
        String contents =
                converter.convert(
                        Context.create(section, epubRenderHolder),
                        markdown
                ).collect(joining());
        byte[] content = contents.getBytes(StandardCharsets.UTF_8);
        return Stream.concat(
                createContent(section, content),
                footnoteHtmlContentGenerator.createFootnotes(section)
        );
    }

    private Stream<Content> createContent(HtmlSection source, byte[] content) {
        return Stream.of(new Content(source.getHref(), content));
    }

}
