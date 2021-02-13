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

    void add(String name, String ordinal, Footnote<T, P> footnote);

    Footnote<T, P> get(String name, String ordinal);

    Stream<Map.Entry<String, List<T>>> streamByName(String name);
}
