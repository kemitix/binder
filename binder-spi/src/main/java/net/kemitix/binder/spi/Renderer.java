package net.kemitix.binder.spi;

import java.util.stream.Stream;

public interface Renderer<T, O> {
    boolean canHandle(String type);

    Stream<O> render(T source);
}
