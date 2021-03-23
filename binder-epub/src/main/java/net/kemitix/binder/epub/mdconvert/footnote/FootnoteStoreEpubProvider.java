package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.FootnoteStore;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
public class FootnoteStoreEpubProvider {

    FootnoteStore<EpubFootnote.Content, EpubFootnote.Placeholder> store =
            FootnoteStore.create(
                    EpubFootnote.Content.class,
                    EpubFootnote.Placeholder.class);

    @Produces
    public EpubFootnoteStore footnoteStore() {
        return new EpubFootnoteStore() {
            @Override
            public void add(
                    Section.Name name,
                    Footnote.Ordinal ordinal,
                    Footnote<EpubFootnote.Content, EpubFootnote.Placeholder> footnote
            ) {
                store.add(name, ordinal, footnote);
            }

            @Override
            public Footnote<EpubFootnote.Content, EpubFootnote.Placeholder> get(
                    Section.Name name,
                    Footnote.Ordinal ordinal) {
                return store.get(name, ordinal);
            }

            @Override
            public Stream<Map.Entry<Footnote.Ordinal, List<EpubFootnote.Content>>>
            streamByName(Section.Name name) {
                return store.streamByName(name);
            }
        };
    }

}
