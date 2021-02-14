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
            public void add(String name, String ordinal, Footnote<P, R> footnote) {
                store.add(name, ordinal, footnote);
            }

            @Override
            public Footnote<P, R> get(String name, String ordinal) {
                return store.get(name, ordinal);
            }

            @Override
            public Stream<Map.Entry<String, List<P>>> streamByName(String name) {
                return store.streamByName(name);
            }
        };
    }

}
