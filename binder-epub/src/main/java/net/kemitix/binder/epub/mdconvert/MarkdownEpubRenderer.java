package net.kemitix.binder.epub.mdconvert;

import coza.opencollab.epub.creator.model.Content;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kemitix.binder.epub.EpubRenderer;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
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
        byte[] content = converter.convert(source)
                .collect(joining())
                .getBytes(StandardCharsets.UTF_8);
        return Stream.concat(
                createContent(source, content),
                createFootnotes(source)
        );
    }

    private Stream<Content> createContent(HtmlSection source, byte[] content) {
        return Stream.of(new Content(source.getHref(), content));
    }

    private Stream<Content> createFootnotes(HtmlSection section) {
        return section.getFootnotes(String.class).stream()
                .flatMap(store -> store.entrySet().stream())
                .map(entry -> {
                    List<String> value = (List<String>) entry.getValue();
                    String collect = String.join("", value);
                    return Tuple.of(entry.getKey(),
                            """
<dl>
<dt><a id="note-%1$s"/>%1$s</dt>
<dd>%2$s</dd>
</dl>
"""
                                    .formatted(entry.getKey(), collect));
                })
                .map(tBody -> tBody.mapRight(body -> body.getBytes(StandardCharsets.UTF_8)))
                .map(tBytes -> tBytes.mapLeft(("footnotes/" + section.getName() + "/footnote-%s.html")::formatted))
                .map(tBytes -> new Content(tBytes.getA(), tBytes.getB()));
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Tuple<A, B> {
        private final A a;
        private final B b;

        public static <A, B> Tuple<A, B> of(A a, B b) {
            return new Tuple<>(a, b);
        }
        public <C> Tuple<C, B> mapLeft(Function<? super A, ? extends C> fn) {
            return Tuple.of(fn.apply(a), b);
        }
        public <C> Tuple<A, C> mapRight(Function<? super B, ? extends C> fn) {
            return Tuple.of(a, fn.apply(b));
        }
    }

}
