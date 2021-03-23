package net.kemitix.binder.epub.mdconvert.footnote;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.epub.mdconvert.Tuple;
import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class FootnoteHtmlContentGenerator {

    private final EpubFootnoteStore footnoteStore;

    @Inject
    public FootnoteHtmlContentGenerator(EpubFootnoteStore footnoteStore) {
        this.footnoteStore = footnoteStore;
    }

    public Stream<Content> createFootnotes(HtmlSection section) {
        Section.Name sectionName = section.getName();
        return footnoteStore
                .streamByName(sectionName)
                .map(Tuple::of)
                .map(t -> t.mapSecond(mergeContent()))
                .map(t -> t.mapSecond(footnoteBody(sectionName)))
                .map(t -> t.mapSecond(asBytes()))
                .map(t -> t.mapFirst(backlink(sectionName)))
                .map(this::asContent)
                .peek(content -> content.setSpine(false))
                .peek(content -> content.setToc(false));
    }

    private Function<List<EpubFootnote.Content>, EpubFootnote.Content> mergeContent() {
        return list ->
                EpubFootnote.content(
                        list.stream()
                                .map(EpubFootnote.Content::getValue)
                                .collect(Collectors.joining()));
    }

    private Function<EpubFootnote.Content, byte[]> asBytes() {
        return s -> s.getValue().getBytes(StandardCharsets.UTF_8);
    }

    private Content asContent(Tuple<String, byte[]> t) {
        String href = t.getFirst();
        byte[] body = t.getSecond();
        return new Content(href, body);
    }

    private Function<Footnote.Ordinal, String> backlink(Section.Name name) {
        return ordinal -> "footnotes/%s/footnote-%s.html"
                .formatted(name, ordinal);
    }

    private BiFunction<Footnote.Ordinal, EpubFootnote.Content, EpubFootnote.Content> footnoteBody(Section.Name name) {
        return (ordinal, body) ->
                EpubFootnote.content(
                        """
                        <dl id="note_%1$s" class="footnote">
                          <dt class="footnote-return">
                            [<a href="../../issue/%3$s.html#back_note_%1$s" title="%1$s" class="footnote-return-link">←%1$s</a>]
                          </dt>
                          <dd class="footnote-body">
                            %2$s
                          </dd>
                        </dl>
                        """.formatted(ordinal, body, name));
    }

}
