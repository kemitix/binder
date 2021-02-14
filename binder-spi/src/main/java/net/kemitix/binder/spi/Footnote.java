package net.kemitix.binder.spi;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Footnote<T, P> {

    static <T, P> Footnote<T, P> create(
            String ordinal,
            P placeholder,
            Stream<T> content
    ) {
        List<T> collect = content.collect(Collectors.toList());
        return new FootnoteImpl<>(ordinal, placeholder, collect);
    }

    String getOrdinal();

    P getPlaceholder();

    List<T> getContent();
}
