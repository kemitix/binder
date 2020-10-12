package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.app.SectionRenderer;

import java.nio.charset.StandardCharsets;

public interface EpubSectionRenderer<T>
        extends SectionRenderer<T, Content> {

    default Content htmlContent(
            String href,
            String html
    ) {
        String mediaType = "application/xhtml+xml";
        return content(href, mediaType, html);
    }

    default Content content(
            String href,
            String mediaType,
            String body
    ) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return new Content(mediaType, href, bytes);
    }

}
