package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.FootnoteStore;

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
                    String name,
                    Footnote.Ordinal ordinal,
                    Footnote<EpubFootnote.Content, EpubFootnote.Placeholder> footnote
            ) {
                store.add(name, ordinal, footnote);
            }

            @Override
            public Footnote<EpubFootnote.Content, EpubFootnote.Placeholder> get(String name, Footnote.Ordinal ordinal) {
                return store.get(name, ordinal);
            }

            @Override
            public Stream<Map.Entry<Footnote.Ordinal, List<EpubFootnote.Content>>> streamByName(String name) {
                return store.streamByName(name);
            }
        };
    }

}
