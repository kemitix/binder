package net.kemitix.binder.docx.mdconvert.footnote;

import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.FootnoteStore;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
public class FootnoteStoreDocxProvider {

    FootnoteStore<P, R> store = FootnoteStore.create(P.class, R.class);

    @Produces
    public DocxFootnoteStore footnoteStore() {
        return new DocxFootnoteStore() {
            @Override
            public void add(String name, Footnote.Ordinal ordinal, Footnote<P, R> footnote) {
                store.add(name, ordinal, footnote);
            }

            @Override
            public Footnote<P, R> get(String name, Footnote.Ordinal ordinal) {
                return store.get(name, ordinal);
            }

            @Override
            public Stream<Map.Entry<Footnote.Ordinal, List<P>>> streamByName(String name) {
                return store.streamByName(name);
            }
        };
    }

}
