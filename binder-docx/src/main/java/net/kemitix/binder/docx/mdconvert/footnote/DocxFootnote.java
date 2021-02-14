package net.kemitix.binder.docx.mdconvert.footnote;

import net.kemitix.binder.spi.Footnote;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.List;
import java.util.stream.Stream;

public interface DocxFootnote
        extends Footnote<P, R> {
    static DocxFootnote createDocx(String ordinal, R placeholder, Stream<P> content) {
        Footnote<P, R> footnote = Footnote.create(ordinal, placeholder, content);
        return new DocxFootnote() {
            @Override
            public String getOrdinal() {
                return footnote.getOrdinal();
            }

            @Override
            public R getPlaceholder() {
                return footnote.getPlaceholder();
            }

            @Override
            public List<P> getContent() {
                return footnote.getContent();
            }
        };
    }
}
