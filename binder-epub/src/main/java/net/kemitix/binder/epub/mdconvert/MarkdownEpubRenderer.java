package net.kemitix.binder.epub.mdconvert;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.epub.EpubRenderer;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Epub
@ApplicationScoped
public class MarkdownEpubRenderer
        implements EpubRenderer {

    private final MarkdownConverter<String> converter;

    @Inject
    public MarkdownEpubRenderer(
            @Epub MarkdownConverter<String> converter
    ) {
        this.converter = converter;
    }

    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public Stream<Content> render(HtmlSection source) {
        Stream<String> converted = converter.convert(source);
        byte[] content = converted.collect(joining()).getBytes(StandardCharsets.UTF_8);
        //TODO - Put each foot note in it's own Content
        return Stream.concat(
                createContent(source, content),
                createFootnotes(source)
        );
    }

    private Stream<Content> createContent(HtmlSection source, byte[] content) {
        return Stream.of(new Content(source.getHref(), content));
    }

    private Stream<Content> createFootnotes(HtmlSection source) {
        String href = source.getName() + "-footnotes.html";
        String body = source.getFootnotes(String.class)
                .stream()
                .flatMap(stringListMap -> stringListMap.entrySet().stream())
                .map(e ->
                {
                    List<String> value = (List<String>) e.getValue();
                    String collect = String.join("", value);
                    return """
<dl>
<dt><a id="note-%1$s"/>%1$s</dt>
<dd>%2$s</dd>
</dl>
"""
                            .formatted(e.getKey(), collect);
                })
                .collect(joining());
        if (body.isEmpty()) {
            return Stream.empty();
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return Stream.of(new Content(href, bytes));
    }

}
