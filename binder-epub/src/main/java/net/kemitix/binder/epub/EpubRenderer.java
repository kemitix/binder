package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Renderer;

import java.nio.charset.StandardCharsets;

public interface EpubRenderer
        extends Renderer<HtmlSection, Content> {

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
