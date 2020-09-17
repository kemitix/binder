package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.Section;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;

@Log
@ApplicationScoped
public class EpubContentFactory {
    public Content create(Section section) {
        byte[] bytes = readFile(section);
        String mediaType = "application/xhtml+xml";
        String href = "content/%s.html".formatted(section.getName());
        log.info(String.format("Created Content: %s (%s) [%d bytes] %s",
                href, mediaType, bytes.length, section.getHtmlFile().getName()));
        return new Content(mediaType, href, bytes);
    }

    private byte[] readFile(Section section) {
        try {
            return Files.readAllBytes(section.getHtmlFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Error reading HTML file %s: %s",
                    section.getName(), e.getMessage()), e);
        }
    }
}
