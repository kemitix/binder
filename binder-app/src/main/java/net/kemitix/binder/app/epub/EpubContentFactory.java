package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;

@Log
@ApplicationScoped
public class EpubContentFactory {

    public Content create(String name, String html) {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        String mediaType = "application/xhtml+xml";
        String href = "content/%s.html".formatted(name);
        log.info(String.format("Created Content: %s (%s) [%d bytes] %s",
                href, mediaType, bytes.length, name));
        return new Content(mediaType, href, bytes);
    }

}
