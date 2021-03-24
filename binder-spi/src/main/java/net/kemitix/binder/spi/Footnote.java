package net.kemitix.binder.spi;

import net.kemitix.mon.TypeAlias;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A footnote.
 *
 * @param <T> the type of the content of the footnote body stream
 * @param <P> the type of the placeholder
 */
public interface Footnote<T, P> {

    static <T, P> Footnote<T, P> create(
            Ordinal ordinal,
            P placeholder,
            Stream<T> content
    ) {
        List<T> collect = content.collect(Collectors.toList());
        return new FootnoteImpl<>(ordinal, placeholder, collect);
    }

    Ordinal getOrdinal();

    P getPlaceholder();

    List<T> getContent();

    static Ordinal ordinal(String value) {
        return new Ordinal(value);
    }

    class Ordinal extends TypeAlias<String> {
        private Ordinal(String value) {
            super(value);
        }
    }

}
