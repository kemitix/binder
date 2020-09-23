package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class HtmlFactory {

    private final Manuscript manuscript;
    private final MarkdownToHtml markdownToHtml;
    private final BinderConfig binderConfig;

    @Inject
    public HtmlFactory(
            BinderConfig binderConfig,
            Manuscript manuscript,
            MarkdownToHtml markdownToHtml
    ) {
        this.binderConfig = binderConfig;
        this.manuscript = manuscript;
        this.markdownToHtml = markdownToHtml;
    }

    /**
     * Creates HTML files for all sections in Manuscript.
     */
    public void createAll() {
        manuscript.getContents()
                .forEach(this::createHtmlForSection);
    }

    /**
     * Creates and HTML file from the section.
     *
     * @param section the section
     */
    private void createHtmlForSection(Section section) {
        String markdown = section.getMarkdown();
        String html = "<html><head><title>%s</title></head><body>%s</body></html>"
                .formatted(
                        section.getTitle(),
                        markdownToHtml.apply(markdown));
        Path binder = binderConfig.getBinderOutputDirectory().toPath();
        Path filename = binder
                .resolve(String.format("%s.%s", section.getName(), "html"));
        section.setHtmlFile(filename.toFile());
        writeHtmlFile(html, filename);
    }

    private Path writeHtmlFile(String html, Path filename) {
        try {
            return Files.writeString(filename, html);
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Error writing HTML file: %s", filename), e);
        }
    }

}
