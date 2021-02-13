package net.kemitix.binder.epub.mdconvert.footnote;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.Tuple;
import net.kemitix.binder.spi.FootnoteStoreImpl;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@ApplicationScoped
public class FootnoteHtmlContentGenerator {

    private final EpubFootnoteStore footnoteStore;

    @Inject
    public FootnoteHtmlContentGenerator(EpubFootnoteStore footnoteStore) {
        this.footnoteStore = footnoteStore;
    }

    public Stream<Content> createFootnotes(HtmlSection section) {
        return footnoteStore
                .streamByName(section.getName())
                .map(Tuple::of)
                .map(t -> t.mapFirst(backlink(section.getName())))
                .map(t -> t.mapSecond(l -> String.join("", l)))
                .map(t -> t.mapSecond(footnoteBody(section.getName())))
                .map(t -> t.mapSecond(asBytes()))
                .map(this::asContent)
                .peek(content -> content.setSpine(false))
                .peek(content -> content.setToc(false));
    }

    private Function<String, byte[]> asBytes() {
        return s -> s.getBytes(StandardCharsets.UTF_8);
    }

    private Content asContent(Tuple<String, byte[]> t) {
        String href = t.getFirst();
        byte[] body = t.getSecond();
        return new Content(href, body);
    }

    private Function<String, String> backlink(String name) {
        return ("footnotes/" + name + "/footnote-%s.html")::formatted;
    }

    private BiFunction<String, String, String> footnoteBody(String name) {
        return (ordinal, body) ->
                """
                        <dl>
                          <dt>
                            <a href="../../issue/%3$s.html#back-link-%1$s">%1$s</a>)
                          </dt>
                          <dd>
                            %2$s
                          </dd>
                        </dl>
                        """
                        .formatted(ordinal, body, name);
    }

}
