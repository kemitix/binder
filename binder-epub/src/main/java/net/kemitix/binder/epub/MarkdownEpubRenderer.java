package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.NoArgsConstructor;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.footnote.FootnoteAsideGenerator;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Epub
@ApplicationScoped
@NoArgsConstructor
public class MarkdownEpubRenderer
        implements EpubRenderer {

    private MarkdownConverter<String, EpubRenderHolder> converter;
    private FootnoteAsideGenerator footnoteAsideGenerator;

    @Inject
    public MarkdownEpubRenderer(
            @Epub MarkdownConverter<String, EpubRenderHolder> converter,
            FootnoteAsideGenerator footnoteAsideGenerator
    ) {
        this.converter = converter;
        this.footnoteAsideGenerator = footnoteAsideGenerator;
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
        EpubRenderHolder epubRenderHolder = EpubRenderHolder.create();
        String contents =
                binderCut(
                        converter.convert(
                                Context.create(section, epubRenderHolder),
                                markdown
                        ).collect(joining()));
        int bodyHtmlLength = "</body>\n</html>\n".length();
        String head = contents.substring(0, contents.length() - bodyHtmlLength);
        String asides = footnoteAsideGenerator.createFootnotes(section);
        String bodyHtml = contents.substring(contents.length() - bodyHtmlLength);
        String contentsWithAsides = head + asides + bodyHtml;
        byte[] content = contentsWithAsides.getBytes(StandardCharsets.UTF_8);
        Stream<Content> supplemental = Stream.empty();
        return Stream.concat(
                createContent(section, content),
                supplemental
        );
    }

    /**
     * Removes any text between lines between the tags BINDER_CUT_START and BINDER_CUT_END.
     *
     * Start:
     *               <p style="text-align: center; page-break-after: avoid;">
     *                 &mdash;&nbsp;_BINDER_CUT_ START&nbsp;&mdash;
     *               </p>
     *
     * End:
     *               <p style="text-align: center; page-break-after: avoid;">
     *                 &mdash;&nbsp;_BINDER_CUT_ END&nbsp;&mdash;
     *               </p>
     *
     * @param content the processed markdown
     * @return the markdown without any cut sections
     */
    private String binderCut(String content) {
        if (!content.contains("_BINDER_CUT_")) {
            return content;
        }
        String[] lines = content.split("\n");
        boolean cut = false;
        boolean cutEnding = false;
        List<String> output = new ArrayList<>();
        for (String line : lines) {
            if (!cut && line.contains("_BINDER_CUT_ START")) {
                cut = true;
                output.remove(output.size() - 1);// remove previous line
                continue;
            }
            if (cut && line.contains("_BINDER_CUT_ END")) {
                cutEnding = true;
                continue;
            }
            if (cutEnding) {
                cutEnding = false;
                cut = false;
                continue;
            }
            if (!cut) {
                output.add(line);
            }
        }
        return String.join("\n", output);
    }

    private Stream<Content> createContent(HtmlSection source, byte[] content) {
        return Stream.of(new Content(source.getHref(), content));
    }

}
