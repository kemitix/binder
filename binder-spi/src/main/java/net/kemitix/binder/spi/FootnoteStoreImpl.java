package net.kemitix.binder.spi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FootnoteStoreImpl<T, P>
        implements FootnoteStore<T, P> {

    // name -> ordinal -> placeholder
    private final Map<String, Map<Footnote.Ordinal, Footnote<T, P>>> stores = new HashMap<>();

    @Override
    public void add(String name, Footnote.Ordinal ordinal, Footnote<T, P> footnote) {
        getStore(name)
                .put(ordinal, footnote);
    }

    private Map<Footnote.Ordinal, Footnote<T, P>> getStore(String name) {
        return stores.computeIfAbsent(name, x -> new HashMap<>());
    }

    @Override
    public Footnote<T, P> get(String name, Footnote.Ordinal ordinal) {
        return Objects.requireNonNull(
                Objects.requireNonNull(getStore(name), "Store for " + name)
                        .get(ordinal), "Footnote for " + name + " - " + ordinal);
    }

    @Override
    public Stream<Map.Entry<Footnote.Ordinal, List<T>>> streamByName(String name) {
        return getStore(name)
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().getContent()));
    }

}
