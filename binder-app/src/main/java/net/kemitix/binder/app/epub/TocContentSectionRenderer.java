package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.SectionRenderer;

import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;

@Log
@ApplicationScoped
public class TocContentSectionRenderer implements SectionRenderer<HtmlSection, Content> {

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public Content render(HtmlSection htmlSection) {
        String html = "Table of Contents goes here";
        String name = "toc";
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        String mediaType = "application/xhtml+xml";
        String href = "content/%s.html".formatted(name);
        log.info(String.format("Created Content: %s (%s) [%d bytes] %s",
                href, mediaType, bytes.length, name));
        return new Content(mediaType, href, bytes);
    }
}
