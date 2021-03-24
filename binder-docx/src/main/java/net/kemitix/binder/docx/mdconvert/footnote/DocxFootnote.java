package net.kemitix.binder.docx.mdconvert.footnote;

import net.kemitix.binder.spi.Footnote;
import net.kemitix.mon.TypeAlias;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.List;
import java.util.stream.Stream;

public interface DocxFootnote
        extends Footnote<DocxFootnote.Content, DocxFootnote.Placeholder> {
    static DocxFootnote createDocx(
            Ordinal ordinal,
            DocxFootnote.Placeholder placeholder,
            Stream<DocxFootnote.Content> content
    ) {
        var footnote = Footnote.create(ordinal, placeholder, content);
        return new DocxFootnote() {
            @Override
            public Ordinal getOrdinal() {
                return footnote.getOrdinal();
            }

            @Override
            public DocxFootnote.Placeholder getPlaceholder() {
                return footnote.getPlaceholder();
            }

            @Override
            public List<DocxFootnote.Content> getContent() {
                return footnote.getContent();
            }
        };
    }

    static Content content(P value) {
        return new Content(value);
    }

    class Content extends TypeAlias<P> {
        private Content(P value) {
            super(value);
        }
    }

    static Placeholder placeholder(R value) {
        return new Placeholder(value);
    }

    class Placeholder extends TypeAlias<R> {
        private Placeholder(R value) {
            super(value);
        }
    }
}
