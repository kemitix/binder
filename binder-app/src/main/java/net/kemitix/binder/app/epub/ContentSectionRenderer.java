package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.SectionRenderer;

import java.nio.charset.StandardCharsets;

@Log
public abstract class ContentSectionRenderer<T>
        implements SectionRenderer<T, Content> {

    Content htmlContent(String name, String href, String html) {
        return content(name, href, "application/xhtml+xml", html);
    }

    Content content(String name, String href, String mediaType, String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        log.info(String.format("Created Content: %s (%s) [%d bytes] %s",
                href, mediaType, bytes.length, name));
        return new Content(mediaType, href, bytes);
    }

}
