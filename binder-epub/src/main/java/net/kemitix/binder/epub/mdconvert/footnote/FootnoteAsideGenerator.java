package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.epub.mdconvert.Tuple;
import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class FootnoteAsideGenerator {

    private final EpubFootnoteStore footnoteStore;

    @Inject
    public FootnoteAsideGenerator(EpubFootnoteStore footnoteStore) {
        this.footnoteStore = footnoteStore;
    }

    public String createFootnotes(HtmlSection section) {
        Section.Name sectionName = section.getName();
        return footnoteStore.streamByName(sectionName)
                .map(Tuple::of)
                .map(t -> t.mapSecond(mergeContent()))
                .map(t -> t.mapSecond(footnoteBody()))
                .sorted(Comparator.comparingInt(t ->
                        Integer.parseInt(t.getFirst().getValue())))
                .map(Tuple::getSecond)
                .collect(Collectors.joining());
    }

    private Function<List<EpubFootnote.Content>, String> mergeContent() {
        return list ->
                list.stream()
                        .map(EpubFootnote.Content::getValue)
                        .collect(Collectors.joining());
    }

    private BiFunction<Footnote.Ordinal, String, String> footnoteBody() {
        return """
                %1$s
                <aside id="note_%1$s" epub:type="footnote">
                    %2$s
                </aside>
                """::formatted;
    }
}
