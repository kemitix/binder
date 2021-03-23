package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.spi.Footnote;
import net.kemitix.mon.TypeAlias;

public interface EpubFootnote
        extends Footnote<EpubFootnote.Content, EpubFootnote.Placeholder> {

    static Content content(String value) {
        return new Content(value);
    }

    class Content extends TypeAlias<String> {
        private Content(String value) {
            super(value);
        }
    }

    static Placeholder placeholder(String value) {
        return new Placeholder(value);
    }

    class Placeholder extends TypeAlias<String> {
        private Placeholder(String value) {
            super(value);
        }
    }

}
