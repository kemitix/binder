package net.kemitix.binder.spi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FootnoteStore<T> {

    // name -> ordinal -> LIST
    private final Map<String, Map<String, List<T>>> stores = new HashMap<>();

    public static <T> FootnoteStore<T> create(Class<? extends T> aClass) {
        return new FootnoteStore<T>();
    }

    public void add(String name, String oridinal, List<T> content) {
        getStore(name)
                .put(oridinal, content);
    }

    private Map<String, List<T>> getStore(String name) {
        return stores.computeIfAbsent(name, x -> new HashMap<>());
    }

    public List<T> get(String name, String ordinal) {
        return Objects.requireNonNullElseGet(
                getStore(name)
                        .get(ordinal),
                Collections::emptyList);
    }

    public Stream<Map.Entry<String, List<T>>> streamByName(String name) {
        return getStore(name)
                .entrySet()
                .stream();
    }
}
