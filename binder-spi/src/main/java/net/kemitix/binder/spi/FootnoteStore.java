package net.kemitix.binder.spi;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface FootnoteStore<T, P> {

    static <T, P> FootnoteStore<T, P> create(
            Class<? extends T> typeClass,
            Class<? extends P> placeholderClass
    ) {
        return new FootnoteStoreImpl<>();
    }

    void add(String name, Footnote.Ordinal ordinal, Footnote<T, P> footnote);

    Footnote<T, P> get(String name, Footnote.Ordinal ordinal);

    Stream<Map.Entry<Footnote.Ordinal, List<T>>> streamByName(String name);
}
