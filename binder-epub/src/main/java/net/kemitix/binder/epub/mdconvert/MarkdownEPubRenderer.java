package net.kemitix.binder.epub.mdconvert;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.epub.EpubRenderer;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@EPub
@ApplicationScoped
public class MarkdownEPubRenderer
        implements EpubRenderer {

    private final MarkdownConverter<String> converter;

    @Inject
    public MarkdownEPubRenderer(
            @EPub MarkdownConverter<String> converter
    ) {
        this.converter = converter;
    }

    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public Content render(HtmlSection source) {
        Stream<String> converted = converter.convert(source.getMarkdown());
        byte[] content = converted.collect(joining()).getBytes(StandardCharsets.UTF_8);
        return new Content(source.getHref(), content);
    }


}
