package net.kemitix.binder.docx.mdconvert.footnote;

import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.FootnoteStore;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
public class FootnoteStoreDocxProvider {

    FootnoteStore<DocxFootnote.Content, DocxFootnote.Placeholder> store =
            FootnoteStore.create(DocxFootnote.Content.class, DocxFootnote.Placeholder.class);

    @Produces
    public DocxFootnoteStore footnoteStore() {
        return new DocxFootnoteStore() {
            @Override
            public void add(
                    Section.Name name,
                    Footnote.Ordinal ordinal,
                    Footnote<DocxFootnote.Content, DocxFootnote.Placeholder> footnote
            ) {
                store.add(name, ordinal, footnote);
            }

            @Override
            public Footnote<DocxFootnote.Content, DocxFootnote.Placeholder> get(
                    Section.Name name,
                    Footnote.Ordinal ordinal
            ) {
                return store.get(name, ordinal);
            }

            @Override
            public Stream<Map.Entry<Footnote.Ordinal, List<DocxFootnote.Content>>>
            streamByName(Section.Name name) {
                return store.streamByName(name);
            }
        };
    }

}
