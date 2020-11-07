package net.kemitix.binder.spi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FootnoteStore {

    private final Map<Class, Map<String, List<?>>> stores = new HashMap<>();

    public static FootnoteStore create() {
        return new FootnoteStore();
    }

    public void add(String oridinal, List<?> content) {
        Class<?> aClass =
                Objects.requireNonNull(content.get(0),
                        "Adding footnote with no content!")
                        .getClass();
        Map<String, List<?>> store = stores.computeIfAbsent(aClass, (ac) -> new HashMap<>());
        store.put(oridinal, content);
    }

    public <T> Map<String, List<?>> get(Class<T> aClass) {
        Map<String, List<?>> stringListMap = stores.get(aClass);
        return stringListMap;
    }
}
