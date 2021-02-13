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

    FootnoteStore<String, String> store = FootnoteStore.create(String.class, String.class);

    @Produces
    public EpubFootnoteStore footnoteStore() {
        return new EpubFootnoteStore() {
            @Override
            public void add(String name, String ordinal, Footnote<String, String> footnote) {
                store.add(name, ordinal, footnote);
            }

            @Override
            public Footnote<String, String> get(String name, String ordinal) {
                return store.get(name, ordinal);
            }

            @Override
            public Stream<Map.Entry<String, List<String>>> streamByName(String name) {
                return store.streamByName(name);
            }
        };
    }

}
