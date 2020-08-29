package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class HtmlFactory {

    private final Manuscript manuscript;
    private final MarkdownToHtml markdownToHtml;

    @Inject
    public HtmlFactory(
            Manuscript manuscript,
            MarkdownToHtml markdownToHtml
    ) {
        this.manuscript = manuscript;
        this.markdownToHtml = markdownToHtml;
    }

    /**
     * Creates HTML files for all sections in Manuscript.
     */
    public void createAll() {
        manuscript.getContents()
                .forEach(this::createSection);
    }

    /**
     * Creates and HTML file from the section.
     *
     * @param section the section
     */
    private void createSection(Section section) {
        String markdown = section.getMarkdown();
        String html = markdownToHtml.apply(markdown);
        section.setHtml(html);
        Path binder = section.getFilename()
                .toPath()
                .getParent()
                .resolve("binder");
        makeDirectory(binder);
        Path filename = binder
                .resolve(String.format("%s.%s", section.getName(), "html"));
        writeHtmlFile(html, filename);
    }

    private void makeDirectory(Path path) {
        File dir = path.toFile();
        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            return;
        }
        if (dir.exists()) {
            throw new RuntimeException(String.format(
                    "Error output directory should be a writable directory: %s",
                    path));
        }
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Error creating output directory: %s", path), e);
        }
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
